package edu.jhu.hlt.concrete.ingesters.conll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    for (File pff : Conll2011.find2(ontonotesDir.toFile(), f -> f.getName().endsWith(".parse"))) {
      Path pf = pff.toPath();
      Matcher m = ANNOTATIONS_PARSE_PATTERN.matcher(pf.toString());
      m.find();
      if (!m.matches())
        continue;
      files++;
      LOGGER.debug("pf={}", pf.toString());
      if (debug)
        System.out.println("pf=" + pf);
      String docId = m.group(1);

      List<String> lines = new ArrayList<>();
      try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(pff)))) {
        for (String line = r.readLine(); line != null; line = r.readLine())
          lines.add(line);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

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
    if (debug)
      System.out.printf("done, read in %d parses in %d documents", sentId2Parse.size(), files);
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
      Stream<List<Conll2011Document>> a = skels.preIngest();
      Stream<List<Conll2011Document>> b = a.map(lcd -> {
        for (Conll2011Document doc : lcd)
          for (Conll2011Sentence s : doc.getSentences())
            setWords(s);
        return lcd;
        });
      Stream<List<Communication>> c = b.map(lcd -> {
          List<Communication> comms = new ArrayList<>();
          for (Conll2011Document cd : lcd)
            comms.add(cd.convertToConcrete());
          return comms;
      });
      Stream<Communication> d = c.map(Conll2011::mergeCommunicationsAsSections);
      return d;
    } catch (IOException e) {
      throw new IngestException(e);
    }
  }
}
