package edu.jhu.hlt.concrete.ingesters.conll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.stream.StreamBasedStreamIngester;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.tutils.PennTreeReader;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;

/**
 * The skel format for Ontonotes 5.0
 * (via https://github.com/ontonotes/conll-formatted-ontonotes-5.0) matches
 * CoNLL2011, but has all of the words as "[WORD]". To get the words, this class
 * reads in the parses from the LDC Ontonotes 5.0 release.
 */
public class Ontonotes5 implements StreamBasedStreamIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(Ontonotes5.class);
  private static final Pattern ANNOTATIONS_PARSE_PATTERN = Pattern.compile(".*annotations/(\\S+).parse");

  private Conll2011 skels;
  private Map<String, PennTreeReader.Indexer> sentId2Parse;

  public boolean debug = false;
  public boolean showAllFileReads = false;

  public Ontonotes5(Conll2011 skels, Path ontonotesDir) throws IOException {
    this(skels, ontonotesDir, false);
  }

  public Ontonotes5(Conll2011 skels, Path ontonotesDir, boolean debug) throws IOException {
    if (!Files.isDirectory(ontonotesDir))
      throw new IllegalArgumentException("not a directory: " + ontonotesDir);
    this.debug = debug;
    // Reading all of these parses takes 5-10 seconds,
    // reading all of the skel files is what takes a lot of time/memory
    LOGGER.info("reading parse information from {}", ontonotesDir.toString());
    int files = 0;
    this.skels = skels;
    this.sentId2Parse = new HashMap<>();
    Stream<Path> targetPaths = Files.list(ontonotesDir)
        .filter(x -> x.endsWith(".parse"));
    for (Path pf : targetPaths.collect(Collectors.toList())) {
      Matcher m = ANNOTATIONS_PARSE_PATTERN.matcher(pf.toString());
      m.find();
      if (!m.matches())
        continue;
      files++;
      LOGGER.debug("pf={}", pf.toString());
      String docId = m.group(1);
      List<String> lines = Files.lines(pf).collect(Collectors.toList());
      StringBuilder sb = new StringBuilder();
      int sent = 0;
      for (int i = 0; i < lines.size(); i++) {
        String l = lines.get(i);
        if (l.isEmpty()) {
          PennTreeReader.Node sexp = PennTreeReader.parse(sb.toString());
          String id = docId + "/" + (sent++);
          LOGGER.debug("id={}", id);
          PennTreeReader.Indexer old = this.sentId2Parse.put(id, new PennTreeReader.Indexer(sexp));
          if (old != null)
            throw new RuntimeException("id=" + id);
          sb = new StringBuilder();
        } else {
          sb.append(l.trim());
        }
      }
    }

    LOGGER.info("done, read in {} parses in {} documents", sentId2Parse.size(), files);
  }

  /**
   * Look up parse for the given {@link Conll2011Sentence} and use it to set the
   * word fields in (before this they all say "[WORD]").
   */
  private void setWords(Conll2011Sentence s) {
    String id = s.getDocId() + "/" + s.getIndex();
    LOGGER.debug("id={}", id);
    PennTreeReader.Indexer root = sentId2Parse.get(id);
    if (root == null)
      throw new RuntimeException();
    List<PennTreeReader.Node> leaves = root.getLeaves(false);
    if (leaves.size() == 0) {
      LOGGER.warn("bogus parse, skipping:"
          + " doc=" + s.getDocId()
          + " part=" + s.getPart()
          + " sent=" + s.getIndex()
          + " leaves.size=" + leaves.size()
          + " s.size=" + s.size()
          + " notraces.size=" + root.getLeaves(false).size());
      return;
    }
    if (leaves.size() != s.size()) {
      for (Conll2011Row w : s.getWords())
        LOGGER.info(w.pos);
      LOGGER.warn("root=" + root.getRoot().getTreeString());
      LOGGER.warn("leaves=" + leaves);
      System.err.flush();
      throw new RuntimeException("doc=" + s.getDocId()
          + " part=" + s.getPart()
          + " sent=" + s.getIndex()
          + " leaves.size=" + leaves.size()
          + " s.size=" + s.size()
          + " notraces.size=" + root.getLeaves(false).size());
    }
    int n = leaves.size();
    for (int i = 0; i < n; i++)
      s.getWord(i).setWord(leaves.get(i).getWord());
  }

  public static void main(String[] args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    String ontonotesRelease5EnglishAnnotationPaths = args[0];
    String skelDataDevelopmentDir = args[1];
    Path ontonotesDir = Paths.get(ontonotesRelease5EnglishAnnotationPaths);
    Path skelDir = Paths.get(skelDataDevelopmentDir);
    Conll2011 skel = new Conll2011(skelDir, f -> f.toString().endsWith(".gold_skel"));
    try {
      Ontonotes5 on5 = new Ontonotes5(skel, ontonotesDir);
      try {
        Stream<Communication> comms = on5.stream();
        for (Communication c : comms.collect(Collectors.toList()))
          LOGGER.info(c.toString());
      } catch (IngestException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
    return Ontonotes5.class.getName();
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
      return skels.preIngest()
          .map(lcd -> {
            // Run setwords on each Conll2011Sentence;
            // retrieved from each Conll2011Document.
            lcd.forEach(doc -> {
              doc.getSentences().stream().forEach(s -> setWords(s));
            });
            return lcd;
          })
          // Map each document to concrete communication
          .map(s -> s.map(cd -> cd.convertToConcrete()))
          // Transform into list, as underlying method is not easily streamable
          .map(sc -> sc.collect(Collectors.toList()))
          // Run mergeCommunicationAsSections on each list
          .map(Conll2011::mergeCommunicationsAsSections);
    } catch (IOException e) {
      throw new IngestException(e);
    }
  }
}
