package edu.jhu.hlt.concrete.ingesters.conll;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.stream.StreamBasedStreamIngester;
import edu.jhu.hlt.concrete.serialization.CommunicationTarGzSerializer;
import edu.jhu.hlt.concrete.serialization.TarGzCompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;

/**
 * Expects a CoNLL-2011 formatted file, which is a single document with multiple
 * parts (each of which is captured as a {@link Section}).
 * <br><br>
 * Adds the following:
 * - Tokenization for the words
 * - TokenTagging for the POS tags
 * - Parse for the constituency parse
 * - SituationMentionSet for the SRL labels
 * - EntitySet and EntityMentionSet for the coref labels
 * - EntityMentionSet for the NER labels (on by default)
 * - TokenTagging for NER labels (on by default)
 * <br><br>
 * Does not (currently) ingest:
 * - word senses
 * - speaker or author
 * <br><br>
 * See: http://conll.cemantix.org/2011/data.html
 * <br><br>
 * Note: This ingester does not attempt to merge the EntityMentionSet produced
 * by the coref annotations with the one generated (optionally) by the NER labels.
 */
public class Conll2011 implements StreamBasedStreamIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(Conll2011.class);

  private static final int kbest = 1;
  private static final long timestamp = Timing.currentLocalTime();
  private static final Pattern p = Pattern.compile("^#begin document \\((\\S+)\\); part (\\S+)$");


  static final AnnotationMetadata META_GENERAL = new AnnotationMetadata("conll-2011", timestamp, kbest);
  static final AnnotationMetadata META_COREF = new AnnotationMetadata("conll-2011 coref", timestamp, kbest);
  static final AnnotationMetadata META_PARSE = new AnnotationMetadata("conll-2011 parse", timestamp, kbest);
  static final AnnotationMetadata META_NER = new AnnotationMetadata("conll-2011 NER", timestamp, kbest);
  static final AnnotationMetadata META_POS = new AnnotationMetadata("conll-2011 POS", timestamp, kbest);
  static final AnnotationMetadata META_SRL = new AnnotationMetadata("conll-2011 SRL", timestamp, kbest);

  static final String SECTION_TYPE = "Passage";

  public boolean addNerAsTokenTagging = true;
  public boolean addNerAsEntityMentionSet = true;
  public boolean includeSingleTokenConstituents = true;

  public boolean includeDebugInfo = false;

  private final Path ingestPath;
  private final Predicate<Path> keep;

  public boolean debug = false;
  public boolean warnOnEmptyCoref = true;

  public Conll2011(Path ingestPath, Predicate<Path> keep) {
    this.ingestPath = ingestPath;
    this.keep = keep;
  }

  public static int count(char c, String s) {
    int count = 0;
    for (char sc : s.toCharArray())
      if (sc == c)
        count++;
    return count;
  }

  /**
   * Merges all of the Communication-level lists ({@link SituationMentionSet},
   * {@link EntitySet}, {@link EntityMentionSet}) from the second arg the first.
   * Expects a single theory on both sides.
   */
  public static void mergeInto(Communication addTo, Communication singleSection) {
    if (singleSection.getSectionListSize() != 1)
      throw new IllegalArgumentException();

    addTo.addToSectionList(singleSection.getSectionList().get(0));

    // Merge SituationMentionSet
    if (addTo.getSituationMentionSetList().size() != 1)
      throw new IllegalArgumentException();
    if (singleSection.getSituationMentionSetList().size() != 1)
      throw new IllegalArgumentException();
    SituationMentionSet toSms = addTo.getSituationMentionSetList().get(0);
    SituationMentionSet fromSms = singleSection.getSituationMentionSetList().get(0);
    for (SituationMention sm : fromSms.getMentionList())
      toSms.addToMentionList(sm);
    // NOTE: This will drop any other data in SituationMentionSet, etc.
    // Thrift doesn't seem to have any workable mergeFrom(thriftObjA, thriftObjB)
    // to support this without dropping some fields.

    // Merge EntitySet
    if (addTo.getEntitySetListSize() != 1)
      throw new IllegalArgumentException();
    if (singleSection.getEntitySetListSize() != 1)
      throw new IllegalArgumentException();
    EntitySet toEs = addTo.getEntitySetList().get(0);
    EntitySet fromEs = singleSection.getEntitySetList().get(0);
    for (Entity e : fromEs.getEntityList())
      toEs.addToEntityList(e);

    // Merge EntityMentionSet
    if (addTo.getEntityMentionSetListSize() !=
        singleSection.getEntityMentionSetListSize()) {
      throw new IllegalArgumentException();
    }
    for (int i = 0; i < addTo.getEntityMentionSetListSize(); i++) {
      EntityMentionSet toEms = addTo.getEntityMentionSetList().get(i);
      EntityMentionSet fromEms = singleSection.getEntityMentionSetList().get(i);
      for (EntityMention em : fromEms.getMentionList())
        toEms.addToMentionList(em);
    }
  }

  public static Communication mergeCommunicationsAsSections(List<Communication> c) {
    Communication all = c.get(0);
    for (int i = 1; i < c.size(); i++) {
      Communication cc = c.get(i);
      if (!all.getId().equals(cc.getId())) {
        throw new IllegalArgumentException("not all ids match, these should be "
            + "sections from the same document and have the same id");
      }
      mergeInto(all, cc);
    }
    return all;
  }

  /**
   * Considers separate parts as separate {@link Conll2011Document}s. You need
   * to merge them into a single {@link Communication} using {@link Section}s.
   * @throws IOException
   */
  public Stream<Stream<Conll2011Document>> preIngest() throws IOException {
    return Files.list(this.ingestPath)
        .filter(this.keep)
        .map(this::readDocuments)
        .map(l -> l.stream());
  }

  private List<Conll2011Document> readDocuments(Path f) {
    LOGGER.debug("reading from {}", f.toString());
    try {
      List<String> lines = Files.lines(f, StandardCharsets.UTF_8)
          .collect(Collectors.toList());
      List<Conll2011Document> documents = new ArrayList<>();
      for (int i = 0; i < lines.size(); i = readDocument(f, lines, i, documents)) {
        if (i < 0)
          throw new RuntimeException();
      }
      return documents;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * @param f is only used for debugInfo, use lines for data
   * @param lines
   * @param start
   * @param addTo
   * @return
   */
  private int readDocument(Path f, List<String> lines, int start, List<Conll2011Document> addTo) {
    String header = lines.get(start);
    Matcher m = p.matcher(header);
    m.find();
    if (!m.matches()) {
      LOGGER.warn("prev=" + lines.get(start - 1));
      LOGGER.warn("head=" + header);
      LOGGER.warn("next=" + lines.get(start + 1));
      throw new RuntimeException();
    }

    // id without a part number
    // NOTE: Not using part numbers because the *.parse files don't have it
    String id = m.group(1);
    // Don't need to get the part info from here, it is in Conll2011Row
    //String part = m.group(2);

    // Count the number of tokens that appear before the tokens in the document
    // about to be read
    int sentenceIndex = 0;
    for (Conll2011Document d : addTo)
      sentenceIndex += d.getSentences().size();

    // I'm stripping the part number from the document ids so it is not unique!
    // Solution: later on these documents are all merged.

    String communicationType = "???";
    Conll2011Document doc = new Conll2011Document(this, id, communicationType);
    addTo.add(doc);
    List<Conll2011Sentence> buf = new ArrayList<>();
    for (int i = start + 1; i < lines.size(); i = readSentence(f, lines, i, sentenceIndex++, buf)) {
      String line = lines.get(i);
      if (line.startsWith("#end document")) {
        for (Conll2011Sentence s : buf)
          doc.add(s);
        return i + 1;
      }
    }
    return -1;
  }

  /**
   * @param f is only used for debugInfo, use lines for data
   * @param lines
   * @param start
   * @param sentenceIndex
   * @param addTo
   * @return
   */
  private int readSentence(Path f, List<String> lines, int start, int sentenceIndex, List<Conll2011Sentence> addTo) {
    Conll2011Sentence s = new Conll2011Sentence(this, sentenceIndex);
    addTo.add(s);
    for (int i = start; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.isEmpty()) {
        return i + 1;
      }
      s.add(new Conll2011Row(line));
    }
    if (includeDebugInfo)
      s.debugInfo = new Conll2011Sentence.DebugInfo(f, start, start + s.size());
    return -1;
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.err.println("please provide:");
      System.err.println("1) an input directory of CoNLL data");
      System.err.println("2) an output Concrete tar gz file");
      System.err.println("3) suffix for the CoNLL files you're looking for (e.g. \".v4_gold_conll\")");
      return;
    }
    Path input = Paths.get(args[0]);
    Path output = Paths.get(args[1]);
    String suffix = args[2];
    if (Files.exists(output)) {
      throw new IllegalArgumentException(
          "output must not exist (this tool won't overwrite): " + output.toString());
    }
    System.out.println("reading from " + input.toString() + " looking for files that end in \"" + suffix + "\"");
    Conll2011 ingester = new Conll2011(input, x -> x.endsWith(suffix));
    Stream<Communication> citer = ingester.stream();
    List<Communication> comms = citer.collect(Collectors.toList());

    System.out.println("writing " + comms.size() + " Communications to " + output.toString());
    CommunicationTarGzSerializer ts = new TarGzCompactCommunicationSerializer();
    ts.toTarGz(comms, output);
    System.out.println("done");
  }

  @Override
  public String getKind() {
    return "document";
  }

  @Override
  public long getTimestamp() {
    return Timing.currentLocalTime();
  }

  @Override
  public String getTool() {
    return Conll2011.class.getSimpleName();
  }

  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  @Override
  public List<String> getToolNotes() {
    return new ArrayList<>();
  }

  @Override
  public Stream<Communication> stream() throws IngestException {
    try {
      return this.preIngest()
          // have Stream<Stream<Conll2011Document>>
          // Convert each conll doc to communication
          .map(lcd -> lcd.map(cd -> cd.convertToConcrete())
              // switch to list for merge method
              .collect(Collectors.toList()))
          // now have Stream<List<Comm>>
          // apply mergeCommunicationsAsSections
          .map(Conll2011::mergeCommunicationsAsSections)
          // map mergeTokensUp
          .map(Conll2011::projectTokenTextSpansUpwards);
    } catch (IOException e) {
      throw new IngestException(e);
    }
  }

  /**
   * Given a {@link Communication} which has {@link Token}s that have their
   * text field set, but not their {@link TextSpan}, build a String for the
   * entire document and create {@link TextSpan}s that point into that for
   * everything above {@link Token}. Sentences go on their own line, and their
   * is an empty line at the end of every section.
   *
   * NOTE: This method should only be used in cases where there is no original
   * text (e.g. CoNLL data which comes word-segmented).
   */
  public static Communication projectTokenTextSpansUpwards(Communication c) {
    if (c.isSetText())
      throw new IllegalArgumentException("text is already set");
    Communication cpy = new Communication(c);
    StringBuilder sb = new StringBuilder();
    for (Section sect : cpy.getSectionList()) {
      int sectionStart = sb.length();
      for (Sentence sent : sect.getSentenceList()) {
        Tokenization tok = sent.getTokenization();
        if (!TokenizationKind.TOKEN_LIST.equals(tok.getKind()))
          throw new IllegalArgumentException("only token lists are supported");
        int sentenceStart = sb.length();
        List<Token> toks = tok.getTokenList().getTokenList();
        for (int i = 0; i < toks.size(); i++) {
          if (i > 0)
            sb.append(' ');
          Token t = toks.get(i);
          if (!t.isSetText())
            throw new IllegalArgumentException("Token text is not set!");
          int start = sb.length();
          sb.append(t.getText());
          int end = sb.length();
          t.setTextSpan(new TextSpan(start, end));
        }
        int sentenceEnd = sb.length();
        if (sent.isSetTextSpan()) {
          boolean s = sentenceStart == sent.getTextSpan().getStart();
          boolean e = sentenceEnd == sent.getTextSpan().getEnding();
          if (!s || !e) {
            throw new RuntimeException("incompatible existing Sentence.textSpan!"
                + " existingStart=" + sent.getTextSpan().getStart()
                + " existingEnd=" + sent.getTextSpan().getEnding()
                + " computedStart=" + sentenceStart
                + " computedEnd=" + sentenceEnd);
          }
        } else {
          sent.setTextSpan(new TextSpan(sentenceStart, sentenceEnd));
        }
        sb.append('\n');
      }
      int sectionEnd = sb.length();
      if (sect.isSetTextSpan()) {
        boolean s = sectionStart == sect.getTextSpan().getStart();
        boolean e = sectionEnd == sect.getTextSpan().getEnding();
        if (!s || !e) {
          throw new RuntimeException("incompatible existing Sentence.textSpan!"
              + " existingStart=" + sect.getTextSpan().getStart()
              + " existingEnd=" + sect.getTextSpan().getEnding()
              + " computedStart=" + sectionStart
              + " computedEnd=" + sectionEnd);
        }
      } else {
        sect.setTextSpan(new TextSpan(sectionStart, sectionEnd));
      }
      sb.append('\n');
    }
    cpy.setText(sb.toString());
    return cpy;
  }
}
