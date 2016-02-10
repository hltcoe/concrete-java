package edu.jhu.hlt.concrete.ingesters.conll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Constituent;
import edu.jhu.hlt.concrete.ConstituentRef;
import edu.jhu.hlt.concrete.MentionArgument;
import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationMentionSet;
import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;
import edu.jhu.hlt.concrete.serialization.TarGzCompactCommunicationSerializer;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.tutils.PennTreeReader;
import edu.jhu.prim.bimap.ObjectObjectBimap;
import edu.jhu.prim.tuple.Pair;

/**
 * Ingests Ontonotes 4 data, currently only storing:
 * - PTB parses (as a {@link Parse})
 * - POS tags from the PTB parse (as a {@link TokenTagging})
 * - Propbank SRL (as a {@link SituationMention})
 * - Word senses (as a {@link TokenTagging}) -- currently sparse
 *
 * Getting this ingester to handle coref data will be tricky because that data
 * is not annotated on top of the PTB-style data, it is SGML overlayed on raw
 * text.
 *
 * The parses produced by this tool will include traces with the "-NONE-" tag.
 * These will also appear in the {@link TokenTagging}s, and traces will have
 * text fields set to the empty string.
 *
 * The SRL labels produced by this ingester will use the "continuation roles"
 * method of handling split arguments. For example if the role "ARG1" is split
 * into two pieces, they will be added as two {@link MentionArgument}s with the
 * first one having a role of "ARG1" and the second "C-ARG1".
 *
 * Arguments which contain traces will be represented by the surface form of the
 * argument, or the end of the trace. The start of the trace will be in the
 * parse tree, but there will be nothing connecting it to the argument.
 *
 * If a predicate is not a constituent (e.g. in "the PTB [keeps on] annoying me"
 * the predicate "keeps on" is not a single constituent), then the
 * {@link SituationMention}'s constituent field is not set. The tokens field is
 * always set.
 *
 * @deprecated This class works but should not be used in favor of the
 * CoNLL-formatted version of Ontonotes, see:
 *   https://github.com/ontonotes/conll-formatted-ontonotes-5.0
 *   {@link Ontonotes5}
 *
 * @author travis
 */
@Deprecated
public class Ontonotes4 {

  // e.g. "ontonotes-release-4.0/data/files/data/english/annotations/bc/cnn/00/cnn_0000"
  private String baseName;
  private String communicationType;
  private String sectionKind;
  private AnnotationMetadata meta;
  private AnnotationMetadata posMeta;

  public Ontonotes4(String baseName, String communicationType, String sectionKind) {
    this.baseName = baseName;
    this.communicationType = communicationType;
    this.sectionKind = sectionKind;
    this.meta = new AnnotationMetadata();
    this.meta.setTimestamp(System.currentTimeMillis() / 1000);
    this.meta.setTool("ontonotes4");
    this.posMeta = new AnnotationMetadata();
    this.posMeta.setTimestamp(this.meta.getTimestamp());
    this.posMeta.setTool("ontonotes4-pos");
  }

  public String getIdFromBaseName() {
    String[] path = baseName.split("/");
    if (path.length < 4)
      throw new RuntimeException("path = " + Arrays.toString(path));
    int k = 4;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append('/');
      sb.append(path[(path.length - k) + i]);
    }
    return sb.toString();
  }

  public boolean hasNeededFiles() {
    return getParseFile().isFile()
        && getPropFile().isFile()
        && getSenseFile().isFile();
  }

  public File getParseFile() {
    return new File(baseName + ".parse");
  }

  public File getPropFile() {
    return new File(baseName + ".prop");
  }

  public File getSenseFile() {
    return new File(baseName + ".sense");
  }

  /**
   * Builds a {@link Communication} with a single section containing a
   * constituency parse, Propbank SRL (as a ???), and word senses as a
   * {@link TokenTagging}.
   */
  public Iterable<Communication> parse() {

    // Read in the data
    List<PennTreeReader.Node> parses = getParses();       // one per sentence
    int nSent = parses.size();
    List<OntonotesProposition>[] props = getProps(nSent); // many per sentence
    List<OntonotesWordsense>[] senses = getSenses(nSent); // many per sentence

    // Populate the Communication
    Communication comm = new Communication();
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator g = f.create();
    comm.setUuid(g.next());
    comm.setId(getIdFromBaseName());
    comm.setType(communicationType);
    comm.setMetadata(meta);

    Section s = new Section();
    s.setUuid(g.next());
    s.setKind(sectionKind);
    comm.addToSectionList(s);

    SituationMentionSet sms = new SituationMentionSet();
    sms.setUuid(g.next());
    sms.setMetadata(meta);
    sms.setMentionList(new ArrayList<>());
    comm.addToSituationMentionSetList(sms);

    for (int i = 0; i < nSent; i++) {
      // Build the sentence based on the parse leaves (including POS tags)
      PennTreeReader.Node root = parses.get(i);
      PennTreeReader.Indexer rootIndex = new PennTreeReader.Indexer(root);
      Sentence sent = makeSentence(rootIndex, g);
      sent.setUuid(g.next());
      s.addToSentenceList(sent);

      // Add senses
      addSenses(sent, senses[i], g);

      // Add parse
      Parse cons = new Parse();
      cons.setUuid(g.next());
      cons.setMetadata(meta);
      sent.getTokenization().addToParseList(cons);
      ObjectObjectBimap<PennTreeReader.Node, Constituent> cmap =
          new ObjectObjectBimap<>();  // Needed for props
      addConstituents(cons, root, cmap, rootIndex);

      // Add propositions
      for (OntonotesProposition p : props[i]) {

        // Build the SituationMention
        SituationMention sm = new SituationMention();
        sm.setUuid(g.next());
        sm.setConfidence(1);

        // Add the predicate
        sm.setSituationKind(p.getPredicateString());
        OntonotesProposition.Proplabel pred = p.predicate;
        Pair<Integer, Integer> predBounds = pred.getSplitsAsContiguousSpan(rootIndex);
        if (predBounds == null) {
          System.err.println("can't handle non-contiguous predicates: " + pred);
          continue;
        }
        sms.addToMentionList(sm);
        sm.setTokens(getTrs(predBounds, sent));
        if (!pred.isSplit()) {
          Constituent predC = findNode(pred, rootIndex, cmap);
          sm.setConstituent(getRef(cons, predC));
        }

        // Add the arguments
        sm.setArgumentList(new ArrayList<>());
        for (OntonotesProposition.Proplabel a : p.args) {

          // Loop over all splits (most of the time there will be only one)
          for (int si = 0; si < a.getNumSplits(); si++) {
            int t = a.getTerminal(si);
            int h = a.getHeight(si);
            PennTreeReader.Node anode = rootIndex.get(t, h);
            Constituent acons = cmap.lookup1(anode);

            String role = a.getLabel();
            if (i > 0)  // Continuation role
              role = "C-" + role;

            MentionArgument arg = new MentionArgument();
            arg.setConfidence(1);
            arg.setRole(a.getLabel());
            arg.setTokens(getTrs(acons.getStart(), acons.getEnding() - 1, sent));
            arg.setConstituent(getRef(cons, acons));
            sm.addToArgumentList(arg);
          }
        }
      }
    }
    return Arrays.asList(comm);
  }

  public static TokenRefSequence getTrs(Pair<Integer, Integer> inclusivePair, Sentence sent) {
    return getTrs(inclusivePair.get1(), inclusivePair.get2(), sent);
  }

  public static TokenRefSequence getTrs(int startInclusive, int endInclusive, Sentence sent) {
    TokenRefSequence trs = new TokenRefSequence();
    trs.setTokenizationId(sent.getTokenization().getUuid());
    for (int i = startInclusive; i <= endInclusive; i++)
      trs.addToTokenIndexList(i);
    return trs;
  }

  /**
   * Use this method for type safety, make sure you don't use the wrong UUID.
   * Also checks that Consituent IDs are equal to their index in parse.consitutentList
   */
  public static ConstituentRef getRef(Parse p, Constituent c) {
    if (p.getConstituentList().get(c.getId()) != c)
      throw new IllegalStateException();
    return new ConstituentRef(p.getUuid(), c.getId());
  }

  /**
   * Looks for node in the tree at root then uses node2cons to convert to a
   * {@link Constituent}.
   */
  public static Constituent findNode(
      OntonotesProposition.Proplabel node,
      PennTreeReader.Indexer tree,
      ObjectObjectBimap<PennTreeReader.Node, Constituent> node2cons) {
    if (node.isSplit())
      throw new IllegalArgumentException("not allowed");
    PennTreeReader.Node n = tree.get(node.getTerminal(), node.getHeight());
    Constituent cnode = node2cons.lookup1(n);
    assert cnode != null : "not a constituent?";
    return cnode;
  }

  /**
   * Recursively adds all Nodes in root as {@link Constituent}s in p.
   * @param bimap may be null, but otherwise will have all node entries added.
   */
  public static Constituent addConstituents(
      Parse p,
      PennTreeReader.Node root,
      ObjectObjectBimap<PennTreeReader.Node, Constituent> bimap,
      PennTreeReader.Indexer indexer) {
    Constituent c = new Constituent();
    c.setId(p.getConstituentListSize());
    c.setTag(root.getCategory());
    c.setStart(indexer.getFirstToken(root));
    c.setEnding(indexer.getLastToken(root) + 1);
    assert c.getEnding() >= 0 && c.getStart() >= 0 && c.getStart() < c.getEnding()
        : "start=" + c.getStart() + " end=" + c.getEnding();
    p.addToConstituentList(c);
    if (bimap != null)
      bimap.put(root, c);
    c.setChildList(new ArrayList<>());
    for (PennTreeReader.Node n : root.getChildren()) {
      Constituent nc = addConstituents(p, n, bimap, indexer);
      c.addToChildList(nc.getId());
    }
    return c;
  }

  /**
   * Makes a {@link TokenList} out of the leaf nodes in the given tree.
   * Will insert a token for a trace which has no text field set. Also adds POS
   * tags as a {@link TokenTagging}.
   * @param newParam TODO
   */
  private Sentence makeSentence(PennTreeReader.Indexer tree, AnalyticUUIDGenerator g) {
    Sentence sent = new Sentence();
    Tokenization tok = new Tokenization();
    tok.setUuid(g.next());
    tok.setMetadata(meta);
    sent.setTokenization(tok);
    tok.setKind(TokenizationKind.TOKEN_LIST);
    TokenList tkl = new TokenList();
    tok.setTokenList(tkl);

    TokenTagging pos = new TokenTagging();
    pos.setUuid(g.next());
    pos.setMetadata(posMeta);
    pos.setTaggingType("pos");
    tok.addToTokenTaggingList(pos);

    int i = 0;
    for (PennTreeReader.Node n : tree.getLeaves(true)) {
      Token t = new Token();
      tkl.addToTokenList(t);
      t.setTokenIndex(i);
      if (n.isTrace())
        t.setText("");
      else
        t.setText(n.getWord());

      TaggedToken tt = new TaggedToken();
      tt.setTag(n.getCategory());
      tt.setTokenIndex(i);
      pos.addToTaggedTokenList(tt);

      i++;
    }

    return sent;
  }

  /**
   * Adds word senses to sent as a {@link TokenTagging} with tags that look like
   * e.g. "work-v-1". The {@link TokenTagging} may not cover all the tokens (you
   * must use the tokenIndex field of TaggedToken).
   */
  private void addSenses(Sentence sent, List<OntonotesWordsense> senses, AnalyticUUIDGenerator g) {
    Tokenization tok = sent.getTokenization();
    TokenTagging tt = new TokenTagging();
    tt.setUuid(g.next());
    tt.setMetadata(meta);
    tok.addToTokenTaggingList(tt);
    tt.setTaggingType("ontonotes-wordsense");
    tt.setTaggedTokenList(new ArrayList<>());
    for (OntonotesWordsense s : senses) {
      TaggedToken tts = new TaggedToken();
      tts.setTokenIndex(s.word);
      tts.setTag(s.getLemmaAndSense());
      tt.addToTaggedTokenList(tts);
    }
  }

  /**
   * Returns an array indexed by sentence, items are tokens appearing in
   * that sentence.
   */
  public List<OntonotesWordsense>[] getSenses(int numSentences) {
    @SuppressWarnings("unchecked")
    List<OntonotesWordsense>[] senses = new List[numSentences];
    for (int i = 0; i < numSentences; i++)
      senses[i] = new ArrayList<>();
    try (BufferedReader r = new BufferedReader(new InputStreamReader(
        new FileInputStream(getSenseFile())))) {
      while (r.ready()) {
        String line = r.readLine();
        OntonotesWordsense ow = new OntonotesWordsense(line);
        senses[ow.sentence].add(ow);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return senses;
  }

  /**
   * Returns an array indexed by sentence, items are propositions appearing in
   * that sentence.
   */
  public List<OntonotesProposition>[] getProps(int numSentences) {
    @SuppressWarnings("unchecked")
    List<OntonotesProposition>[] props = new List[numSentences];
    for (int i = 0; i < numSentences; i++)
      props[i] = new ArrayList<>();
    try (BufferedReader r = new BufferedReader(new InputStreamReader(
        new FileInputStream(getPropFile())))) {
      while (r.ready()) {
        String line = r.readLine();
        OntonotesProposition op = new OntonotesProposition(line, false);
        props[op.sentence].add(op);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return props;
  }

  /**
   * Each node in the returned list is the root of a tree for a given sentence.
   */
  public List<PennTreeReader.Node> getParses() {
    // Read the text in (separated by empty lines)
    List<String> sexps = new ArrayList<>();
    StringBuilder curSexp = new StringBuilder();
    try (BufferedReader r = new BufferedReader(new InputStreamReader(
        new FileInputStream(getParseFile())))) {
      while (r.ready()) {
        String line = r.readLine();
        if (line.isEmpty()) {
          sexps.add(curSexp.toString().replaceAll("\\s+", " "));
          curSexp = new StringBuilder();
        } else {
          curSexp.append(line);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Parse the text
    List<PennTreeReader.Node> parses = new ArrayList<>();
    for (String sexp : sexps)
      parses.add(PennTreeReader.parse(sexp));

    return parses;
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("please provide:");
      System.err.println("1) an Ontonotes 4.0 directory (e.g. ontonotes-release-4.0/data/files/data/english/annotations)");
      System.err.println("2) a Concrete tar.gz file to dump results to");
      System.exit(-1);
    }
    String sectionKind = "body";
    File onDir = new File(args[0]);
    File outputFile = new File(args[1]);

    long start = System.currentTimeMillis();

    System.out.println("reading from " + onDir.getPath());
    List<Communication> all = parseAll(onDir, sectionKind);
    System.out.println("read " + all.size() + " documents in "
        + (System.currentTimeMillis() - start)/1000d + " seconds");

    System.out.println("writing to " + outputFile.getPath());
    TarGzCompactCommunicationSerializer ts = new TarGzCompactCommunicationSerializer();
    ts.toTarGz(all, outputFile.toPath());

    System.out.println("done, took " + (System.currentTimeMillis() - start)/1000d + " seconds");
  }

  public static List<Communication> parseAll(File onDir, String sectionKind) {
    List<Communication> all = new ArrayList<>();
    for (File bc : onDir.listFiles()) {
      if (bc.listFiles() == null) {
        System.err.println("WARN: skipping " + bc.getPath());
        continue;
      }
      for (File cnn : bc.listFiles()) {
        if (cnn.listFiles() == null) {
          System.err.println("WARN: skipping " + cnn.getPath());
          continue;
        }
        for (File zero : cnn.listFiles()) {
          if (cnn.listFiles() == null) {
            System.err.println("WARN: skipping " + cnn.getPath());
            continue;
          }
          for (File f : zero.listFiles()) {
            if (!f.getName().endsWith(".prop"))
              continue;
            String baseName = f.getPath().replaceAll(".prop$", "");
            Ontonotes4 on4 = new Ontonotes4(baseName, bc.getName(), sectionKind);
            if (!on4.hasNeededFiles()) {
              System.err.println("missing files in " + baseName);
              continue;
            }
            for (Communication c : on4.parse())
              all.add(c);
          }
        }
      }
    }
    return all;
  }

  public static void test(String[] args) throws Exception {
    long start = System.currentTimeMillis();
    String baseName = "ontonotes-release-4.0/data/files/data/english/annotations/bc/cnn/00/cnn_0000";
    baseName = "/home/travis/code/fnparse/data/" + baseName;
    Ontonotes4 on4 = new Ontonotes4(baseName, "test-document", "body");
    Communication c = on4.parse().iterator().next();

    TarGzCompactCommunicationSerializer ts = new TarGzCompactCommunicationSerializer();
    ts.toTarGz(Arrays.asList(c), "/tmp/foo.concrete.gz");

//    System.out.println(c);
    System.out.println(System.currentTimeMillis() - start);
  }
}
