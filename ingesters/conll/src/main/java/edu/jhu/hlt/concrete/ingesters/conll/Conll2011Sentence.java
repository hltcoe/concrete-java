package edu.jhu.hlt.concrete.ingesters.conll;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.MentionArgument;
import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/** Many Rows (words) comprising a Sentence */
public class Conll2011Sentence {

  private static final Logger LOGGER = LoggerFactory.getLogger(Conll2011Sentence.class);

  /** Information for tracking back to a file */
  public static class DebugInfo {
    public final int firstLine, lastLine;
    public final Path source;
    public DebugInfo(Path source, int firstLine, int lastLine) {
      this.source = source;
      this.firstLine = firstLine;
      this.lastLine = lastLine;
    }
  }
  public DebugInfo debugInfo = null;

  private final Conll2011 conll2011;
  private int index;
  private String docId;
  private List<Conll2011Row> words;
  private List<EntityMention> nerEMs;
  private Tokenization toks;

  public Conll2011Sentence(Conll2011 conll2011, int index) {
    this.conll2011 = conll2011;
    this.index = index;
    this.words = new ArrayList<>();
    if (this.conll2011.addNerAsEntityMentionSet)
      nerEMs = new ArrayList<>();
  }

  public int getIndex() { return index; }

  public String getDocId() { return docId; }

  public void add(Conll2011Row word) {
    if (docId == null) {
      docId = word.docId;
    } else {
      // Check that things that shouldn't vary between words don't vary
      if (!docId.equals(word.docId))
        throw new IllegalArgumentException();
      if (words.get(0).getNumPredicates() != word.getNumPredicates())
        throw new IllegalArgumentException();
    }
    words.add(word);
  }

  public int size() {
    return words.size();
  }

  public String getPart() {
    return words.get(0).part;
  }

  public Conll2011Row getWord(int i) {
    return words.get(i);
  }

  public List<Conll2011Row> getWords() {
    return words;
  }

  /** You have to call convertToConcrete first */
  public List<EntityMention> getNerEntityMentions() {
    return nerEMs;
  }

  /**
   * Returns a map with coref cluster ids for keys and mentions in the
   * values.
   *
   * NOTE: Coref annotations are only valid up to a part, so we take the id to
   * be "${PART_ID}-${CLUSTER}".
   *
   * NOTE: Coref mentions may overlap and DO NOT form a tree.
   *
   * NOTE: Coref mentions may nest however, e.g. (0 someone who makes (0 his) own luck)
   */
  public Map<String, List<EntityMention>> getCoref(AnalyticUUIDGenerator g) {
    Map<String, Deque<Integer>> openMentions = new HashMap<>();
    Map<String, List<EntityMention>> corefMentions = new HashMap<>();
    for (int i = 0; i < words.size(); i++) {
      Conll2011Row w = words.get(i);
      for (String clusterId : w.getCorefClusterStarts()) {
        Deque<Integer> stack = openMentions.get(clusterId);
        if (stack == null) {
          stack = new ArrayDeque<>();
          openMentions.put(clusterId, stack);
        }
        stack.push(i);
      }
      for (String clusterId : w.getCorefClusterEnds()) {
        Deque<Integer> stack = openMentions.remove(clusterId);
        int start = stack.pop();
        if (stack.size() > 0)
          openMentions.put(clusterId, stack);
        // Make an EntityMention
        EntityMention em = new EntityMention();
        em.setUuid(g.next());
        em.setConfidence(1);
        TokenRefSequence trs = new TokenRefSequence();
        trs.setTokenizationId(toks.getUuid());
        for (int ii = start; ii <= i; ii++)
          trs.addToTokenIndexList(ii);
        //System.err.println("cluster=" + clusterId + " mention=\"" + words + "\"");
        em.setTokens(trs);
        // Add the EntityMention the running set of clusters
        List<EntityMention> ems = corefMentions.get(clusterId);
        if (ems == null) {
          ems = new ArrayList<>();
          corefMentions.put(clusterId, ems);
        }
        ems.add(em);
      }
    }
    assert openMentions.isEmpty();

    // Convert between raw cluster ids, which are only relevant up to a
    // "part" or section of the document to that cluster id conjoined
    // with the part id.
    String partId = getPart();
    Map<String, List<EntityMention>> fixedClusterNames = new HashMap<>();
    for (Map.Entry<String, List<EntityMention>> x : corefMentions.entrySet()) {
      String key = partId + "-" + x.getKey();
      fixedClusterNames.put(key, x.getValue());
    }
    return fixedClusterNames;
  }

  public int getNumPredicates() {
    if (words.isEmpty() ||
        (words.size() == 1 && words.get(0).pos.startsWith("X"))) {
      LOGGER.warn("special case of empty sentence/no predicates "
          + " doc=" + getDocId() + " index=" + index + " part=" + getPart());
      return 0;
    }
    return words.get(0).getNumPredicates();
  }

  public SituationMention getPredArg(int index, AnalyticUUIDGenerator g) {
    if (index < 0 || index >= getNumPredicates())
      throw new IllegalArgumentException();
    if (toks == null)
      throw new IllegalStateException();
    ParseWrapper helper = new ParseWrapper();
    for (int i = 0; i < words.size(); i++) {
      Conll2011Row w = words.get(i);
      helper.add(w.getPredArg(index), null);
    }
    SituationMention sm = new SituationMention();
    sm.setUuid(g.next());
    sm.setConfidence(1);
    sm.setArgumentList(new ArrayList<>());
    for (ParseWrapper.ConstituentWrapper c : helper.getConstituents()) {
      if (c.getTag().equals("V")) {
        // Root (target/verb)
        Conll2011Row w = words.get(c.getStart());
        //String predId = w.predicateLemma + "." + w.predicateFramesetId;
        String predId = w.predicateLemma
            + "-" + w.pos.substring(0, 1).toLowerCase()
            + "-" + w.predicateFramesetId.replaceFirst("^0+", "");
        TokenRefSequence trs = new TokenRefSequence();
        trs.setTokenizationId(toks.getUuid());
        assert c.getStart() == c.getEnd();
        trs.setTokenIndexList(Arrays.asList(c.getStart()));
        sm.setTokens(trs);
        sm.setSituationKind(predId);
        sm.setText(w.getWord());
      } else {
        // Arg
        if (!c.getTag().contains("ARG")) {
          LOGGER.warn("bad arg name: " + c.getTag()
              + " doc=" + getDocId()
              + " sent=" + getIndex()
              + " part=" + getPart()
              + " words=" + words);
        }
        MentionArgument arg = new MentionArgument();
        arg.setRole(c.getTag());
        arg.setConfidence(1);
        arg.setSituationMentionId(sm.getUuid());
        TokenRefSequence trs = new TokenRefSequence();
        trs.setTokenizationId(toks.getUuid());
        for (int ti = c.getStart(); ti <= c.getEnd(); ti++)
          trs.addToTokenIndexList(ti);
        arg.setTokens(trs);
        sm.addToArgumentList(arg);
      }
    }
    assert sm.getTokens() != null;
    return sm;
  }

  public edu.jhu.hlt.concrete.Sentence convertToConcrete(AnalyticUUIDGenerator g) {
    if (nerEMs != null)
      nerEMs.clear();

    edu.jhu.hlt.concrete.Sentence s = new edu.jhu.hlt.concrete.Sentence();
    s.setUuid(g.next());
    if (toks != null)
      System.err.println("double generating Tokenization, may have orphaned SituationMentions!");
    toks = new Tokenization();
    toks.setUuid(g.next());
    toks.setKind(TokenizationKind.TOKEN_LIST);
    toks.setMetadata(Conll2011.META_GENERAL);

    // Tokens/words
    TokenList tl = new TokenList();
    for (int i = 0; i < words.size(); i++) {
      Conll2011Row w = words.get(i);
      Token t = new Token();
      t.setText(w.getWord());
      t.setTokenIndex(i);
      tl.addToTokenList(t);
    }
    toks.setTokenList(tl);

    // POS
    TokenTagging pos = new TokenTagging();
    pos.setUuid(g.next());
    pos.setTaggingType("POS");
    pos.setMetadata(Conll2011.META_POS);
    for (int i = 0; i < words.size(); i++) {
      Conll2011Row w = words.get(i);
      TaggedToken tt = new TaggedToken();
      tt.setConfidence(1);
      tt.setTag(w.pos);
      tt.setTokenIndex(i);
      pos.addToTaggedTokenList(tt);
    }
    toks.addToTokenTaggingList(pos);

    // Constituency parse
    ParseWrapper parseHelper = new ParseWrapper();
    for (int i = 0; i < words.size(); i++) {
      Conll2011Row w = words.get(i);
      // Option 1: Detect when children are a mix of terminals and non-terminals
      // Option 2: Traverse the tree, and any time you see (lhs (lhs word)) => (lhs word)
      if (this.conll2011.includeSingleTokenConstituents) {
        //parseHelper.add(w.parseBit, w.pos);
        parseHelper.addAlt(w.parseBit, w.pos, w.getWord());
      } else {
        parseHelper.add(w.parseBit, null);
      }
    }
    edu.jhu.hlt.concrete.Parse p = new edu.jhu.hlt.concrete.Parse();
    p.setUuid(g.next());
    p.setMetadata(Conll2011.META_PARSE);
    for (ParseWrapper.ConstituentWrapper c : parseHelper.getConstituents())
      p.addToConstituentList(c.convertToConcrete());
    toks.addToParseList(p);

    // NER as TokenTagging
    if (this.conll2011.addNerAsTokenTagging || this.conll2011.addNerAsEntityMentionSet) {

      TokenTagging nerTT = null;
      if (this.conll2011.addNerAsTokenTagging) {
        nerTT = new TokenTagging();
        nerTT.setUuid(g.next());
        nerTT.setMetadata(Conll2011.META_NER);
        nerTT.setTaggingType("NER");
      }

      int start = -1;
      String tag = null;
      for (int i = 0; i < words.size(); i++) {
        Conll2011Row w = words.get(i);
        parseHelper.add(w.namedEntities, null);

        if (tag == null) {
          String[] open = parseHelper.getOpenTags();
          assert open.length < 2;
          if (open.length == 1) {
            tag = open[0];
            start = i;
          }
        }

        TaggedToken tt = new TaggedToken();
        tt.setConfidence(1);
        tt.setTag(tag == null ? "O" : tag);
        tt.setTokenIndex(i);
        nerTT.addToTaggedTokenList(tt);

        int nc = parseHelper.getNumCloseTags();
        assert nc < 2;
        if (nc > 0) {
          if (nerEMs != null) {
            EntityMention em = new EntityMention();
            em.setUuid(g.next());
            em.setConfidence(1);
            em.setEntityType(tag);
            TokenRefSequence trs = new TokenRefSequence();
            trs.setTokenizationId(toks.getUuid());
            for (int ii = start; ii <= i; ii++)
              trs.addToTokenIndexList(ii);
            em.setTokens(trs);
            nerEMs.add(em);
          }
          tag = null;
        }
      }
      toks.addToTokenTaggingList(nerTT);
    }

    s.setTokenization(toks);
    return s;
  }
}
