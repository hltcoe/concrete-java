package edu.jhu.hlt.concrete.ingest.conll;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingest.Ingester;
import edu.jhu.hlt.tutils.LazyIteration.FIterable;
import edu.jhu.hlt.tutils.Log;
import edu.jhu.hlt.tutils.PennTreeReader;

/**
 * The skel format for Ontonotes 5.0
 * (via https://github.com/ontonotes/conll-formatted-ontonotes-5.0) matches
 * CoNLL2011, but has all of the words as "[WORD]". To get the words, this class
 * reads in the parses from the LDC Ontonotes 5.0 release.
 *
 * @author travis
 */
public class Ontonotes5 implements Ingester {

  private Conll2011 skels;
  private Map<String, PennTreeReader.Indexer> sentId2Parse;

  public boolean debug = false;
  public boolean showAllFileReads = false;

  public Ontonotes5(Conll2011 skels, File ontonotesDir) {
    this(skels, ontonotesDir, false);
  }

  public Ontonotes5(Conll2011 skels, File ontonotesDir, boolean debug) {
    if (!ontonotesDir.isDirectory())
      throw new IllegalArgumentException();
    this.debug = debug;
    // Reading all of these parses takes 5-10 seconds,
    // reading all of the skel files is what takes a lot of time/memory
    Log.info("reading parse information from " + ontonotesDir.getPath());
    int files = 0;
    this.skels = skels;
    this.sentId2Parse = new HashMap<>();
    Pattern p = Pattern.compile(".*annotations/(\\S+).parse");
    for (File pf : Conll2011.find(ontonotesDir, x -> x.getName().endsWith(".parse"))) {
      Matcher m = p.matcher(pf.getPath());
      m.find();
      if (!m.matches())
        continue;
      files++;
      if (debug)
        Log.info("pf=" + pf.getPath());
      String docId = m.group(1);
      List<String> lines = Conll2011.readLines(pf);
      StringBuilder sb = new StringBuilder();
      int sent = 0;
      for (int i = 0; i < lines.size(); i++) {
        String l = lines.get(i);
        if (l.isEmpty()) {
          PennTreeReader.Node sexp = PennTreeReader.parse(sb.toString());
          String id = docId + "/" + (sent++);
          if (debug)
            Log.info("id=" + id);
          PennTreeReader.Indexer old = this.sentId2Parse.put(id, new PennTreeReader.Indexer(sexp));
          if (old != null)
            throw new RuntimeException("id=" + id);
          sb = new StringBuilder();
        } else {
          sb.append(l.trim());
        }
      }
    }
    Log.info("done, read in " + sentId2Parse.size() + " parses in " + files + " documents");
  }

  @Override
  public Iterable<Communication> ingest(File root) {

    Iterable<List<Conll2011Document>> ungroupedDocs = skels.preIngest(root);

    Iterable<List<Conll2011Document>> ungroupedDocsFixed =
        new FIterable<>(ungroupedDocs, lcd -> {
          for (Conll2011Document cd : lcd)
            for (Conll2011Sentence s : cd.getSentences())
              setWords(s);
          return lcd;
        });

    Iterable<List<Communication>> ungroupedComms =
        new FIterable<>(ungroupedDocsFixed, lcd -> {
          List<Communication> lc = new ArrayList<>();
          for (Conll2011Document cd : lcd)
            lc.add(cd.convertToConcrete());
          return lc;
        });

    Iterable<Communication> comms =
        new FIterable<>(ungroupedComms, Conll2011::mergeCommunicationsAsSections);

    return comms;
  }

  /**
   * Look up parse for the given {@link Conll2011Sentence} and use it to set the
   * word fields in (before this they all say "[WORD]").
   */
  private void setWords(Conll2011Sentence s) {
    String id = s.getDocId() + "/" + s.getIndex();
    if (debug)
      Log.info("id=" + id);
    PennTreeReader.Indexer root = sentId2Parse.get(id);
    if (root == null)
      throw new RuntimeException();
    List<PennTreeReader.Node> leaves = root.getLeaves(false);
    if (leaves.size() == 0) {
      Log.warn("bogus parse, skipping:"
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
        Log.info(w.pos);
      Log.warn("root=" + root.getRoot().getTreeString());
      Log.warn("leaves=" + leaves);
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
    File ontonotesDir = new File("/home/travis/code/fnparse/data/ontonotes-release-5.0/LDC2013T19/data/files/data/english/annotations");
    //File skelDir = new File("/home/travis/code/conll-formatted-ontonotes-5.0/conll-formatted-ontonotes-5.0/data/train");
    File skelDir = new File("/home/travis/code/conll-formatted-ontonotes-5.0/conll-formatted-ontonotes-5.0/data/development");
    Conll2011 skel = new Conll2011(f -> f.getName().endsWith(".gold_skel"));
    Ontonotes5 on5 = new Ontonotes5(skel, ontonotesDir);
    for (Communication c : on5.ingest(skelDir)) {
      Log.info(c);
    }
  }
}
