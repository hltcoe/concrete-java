package edu.jhu.hlt.concrete.ingest.conll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.ingest.CommunicationUtils;
import edu.jhu.hlt.concrete.ingest.Ingester;
import edu.jhu.hlt.concrete.serialization.CommunicationTarGzSerializer;
import edu.jhu.hlt.concrete.serialization.TarGzCompactCommunicationSerializer;
import edu.jhu.hlt.tutils.LazyIteration.FIterable;
import edu.jhu.hlt.tutils.Log;

/**
 * Expects a CoNLL-2011 formatted file, which is a single document with multiple
 * parts (each of which is captured as a {@link Section}).
 *
 * Adds the following:
 * - Tokenization for the words
 * - TokenTagging for the POS tags
 * - Parse for the constituency parse
 * - SituationMentionSet for the SRL labels
 * - EntitySet and EntityMentionSet for the coref labels
 * - EntityMentionSet for the NER labels (on by default)
 * - TokenTagging for NER labels (on by default)
 *
 * Does not (currently) ingest:
 * - word senses
 * - speaker or author
 *
 * See: http://conll.cemantix.org/2011/data.html
 *
 * Note: This ingester does not attempt to merge the EntityMentionSet produced
 * by the coref annotations with the one generated (optionally) by the NER labels.
 *
 * @author travis
 */
public class Conll2011 implements Ingester {

  private static final int kbest = 1;
  private static final long timestamp = System.currentTimeMillis() / 1000;

  public static final AnnotationMetadata META_GENERAL = new AnnotationMetadata("conll-2011", timestamp, kbest);
  public static final AnnotationMetadata META_COREF = new AnnotationMetadata("conll-2011 coref", timestamp, kbest);
  public static final AnnotationMetadata META_PARSE = new AnnotationMetadata("conll-2011 parse", timestamp, kbest);
  public static final AnnotationMetadata META_NER = new AnnotationMetadata("conll-2011 NER", timestamp, kbest);
  public static final AnnotationMetadata META_POS = new AnnotationMetadata("conll-2011 POS", timestamp, kbest);
  public static final AnnotationMetadata META_SRL = new AnnotationMetadata("conll-2011 SRL", timestamp, kbest);

  public static final String SECTION_TYPE = "Passage";
  public static final String COMMUNICATION_TYPE = "Document";
  public static boolean PREVENT_OVERWRITES = false;

  public boolean addNerAsTokenTagging = true;
  public boolean addNerAsEntityMentionSet = true;
  public boolean includeSingleTokenConstituents = true;

  public boolean includeDebugInfo = false;
  public boolean showAllFileReads = false;

  public Predicate<File> keep;
  public boolean debug = false;

  public Conll2011(Predicate<File> keep) {
    this.keep = keep;
  }

  public static int count(char c, String s) {
    int count = 0;
    for (char sc : s.toCharArray())
      if (sc == c)
        count++;
    return count;
  }

  public static List<File> find(File root, Predicate<File> keep) {
    List<File> all = new ArrayList<>();
    findHelper(root, keep, all);
    return all;
  }
  private static void findHelper(File root, Predicate<File> keep, List<File> addTo) {
    if (keep.test(root))
      addTo.add(root);
    File[] files = root.listFiles();
    if (files == null)
      return;
    for (File f : files)
      findHelper(f, keep, addTo);
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
   */
  public Iterable<List<Conll2011Document>> preIngest(File root) {

    List<File> files = find(root, keep);

    Iterable<List<Conll2011Document>> ungrouped =
        new FIterable<>(files, f -> readDocuments(f));

    return ungrouped;
  }

  @Override
  public Iterable<Communication> ingest(File root) {

    Iterable<List<Conll2011Document>> ungroupedDocs = preIngest(root);

    Iterable<Communication> comms =
        new FIterable<>(ungroupedDocs, lcd -> {
          List<Communication> lc = new ArrayList<>();
          for (Conll2011Document d : lcd)
            lc.add(d.convertToConcrete());
          return mergeCommunicationsAsSections(lc);
        });

    Iterable<Communication> withTextSet =
        new FIterable<>(comms, c -> {
          CommunicationUtils.projectTokenTextSpansUpwards(c);
          return c;
        });

    return withTextSet;
  }

  public List<Conll2011Document> readDocuments(File f) {
    if (showAllFileReads)
      Log.info("reading from " + f.getPath());
    List<String> lines = readLines(f);
    List<Conll2011Document> documents = new ArrayList<>();
    for (int i = 0; i < lines.size(); i = readDocument(f, lines, i, documents)) {
      if (i < 0)
        throw new RuntimeException();
//      Log.info("about to read another doc " + i + " line=" + lines.get(i));
    }
    return documents;
  }

  /**
   * @param f is only used for debugInfo, use lines for data
   * @param lines
   * @param start
   * @param addTo
   * @return
   */
  public int readDocument(File f, List<String> lines, int start, List<Conll2011Document> addTo) {
    Pattern p = Pattern.compile("^#begin document \\((\\S+)\\); part (\\S+)$");
    String header = lines.get(start);
    Matcher m = p.matcher(header);
    m.find();
    if (!m.matches()) {
      Log.warn("prev=" + lines.get(start - 1));
      Log.warn("head=" + header);
      Log.warn("next=" + lines.get(start + 1));
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
  public int readSentence(File f, List<String> lines, int start, int sentenceIndex, List<Conll2011Sentence> addTo) {
    //Conll2011Sentence s = new Conll2011Sentence(this, addTo.size());
    Conll2011Sentence s = new Conll2011Sentence(this, sentenceIndex);
    addTo.add(s);
//    Log.info("starting with " + start + " line=" + lines.get(start));
    for (int i = start; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.isEmpty()) {
//        Log.info("finishing with " + (i+1) + " line=" + lines.get(i+1));
        return i + 1;
      }
      s.add(new Conll2011Row(line));
    }
    if (includeDebugInfo)
      s.debugInfo = new Conll2011Sentence.DebugInfo(f, start, start + s.size());
    return -1;
  }

  public static List<String> readLines(File f) {
    List<String> lines = new ArrayList<>();
    try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
      while (r.ready())
        lines.add(r.readLine());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return lines;
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 3) {
      System.err.println("please provide:");
      System.err.println("1) an input directory of CoNLL data");
      System.err.println("2) an output Concrete tar gz file");
      System.err.println("3) suffix for the CoNLL files you're looking for (e.g. \".v4_gold_conll\")");
      return;
    }
    File input = new File(args[0]);
    File output = new File(args[1]);
    String suffix = args[2];
    if (PREVENT_OVERWRITES && output.exists()) {
      throw new IllegalArgumentException(
          "output must not exist (this tool won't overwrite): " + output.getPath());
    }
    System.out.println("reading from " + input.getPath() + " looking for files that end in \"" + suffix + "\"");
    Conll2011 ingester = new Conll2011(x -> x.getName().endsWith(suffix));
    List<Communication> comms = new ArrayList<>();
    for (Communication c : ingester.ingest(input))
      comms.add(c);
    System.out.println("writing " + comms.size() + " Communications too " + output.getPath());
    CommunicationTarGzSerializer ts = new TarGzCompactCommunicationSerializer();
    ts.toTarGz(comms, output.toPath());
    System.out.println("done");
  }
}
