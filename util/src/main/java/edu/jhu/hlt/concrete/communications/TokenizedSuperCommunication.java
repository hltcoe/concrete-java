/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.communications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Constituent;
import edu.jhu.hlt.concrete.Dependency;
import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;

/**
 * @author max
 *
 */
public class TokenizedSuperCommunication extends SentencedSuperCommunication {

  protected final Map<UUID, Tokenization> tokenizationIdToTokenizationMap;
  protected final Map<UUID, Map<Integer, Token>> tokenizationIdToTokenIdxToTokenMap;
  protected final Map<UUID, TokenTagging> idToTokenTaggingMap;
  protected final Map<String, List<TokenTagging>> ttTypeToTTListMap;
  
  /**
   * @param comm
   */
  public TokenizedSuperCommunication(Communication comm) {
    super(comm);
    this.tokenizationIdToTokenizationMap = this.tokenizationIdToTokenizationMap();
    this.tokenizationIdToTokenIdxToTokenMap = this.tokenizationIdToTokenSeqIdToTokensMap();
    
    this.idToTokenTaggingMap = new HashMap<>();
    this.ttTypeToTTListMap = new HashMap<>();
    
    for (Tokenization tokeniz : this.tokenizationIdToTokenizationMap.values()) {
      for (TokenTagging tt : tokeniz.getTokenTaggingList()) {
        this.idToTokenTaggingMap.put(tt.getUuid(), tt);
        String type = tt.getTaggingType();
        if (this.ttTypeToTTListMap.containsKey(type)) {
          List<TokenTagging> curr = this.ttTypeToTTListMap.get(type);
          curr.add(tt);
        } else {
          List<TokenTagging> init = new ArrayList<>();
          init.add(tt);
          this.ttTypeToTTListMap.put(type, init);
        }
      }
    }
  }

  private final Map<UUID, Tokenization> tokenizationIdToTokenizationMap() {
    final Map<UUID, Tokenization> toRet = new HashMap<>();
    List<Sentence> stList = new ArrayList<>(this.sentIdToSentenceMap.values());
    for (Sentence st : stList) {
      Tokenization tok = st.getTokenization();
      UUID tId = tok.getUuid();
      toRet.put(tId, tok);
    }
      
    return toRet;
  }
  
  /**
   * Returns a nested map. <br>
   * <br>
   * Top level: <br>
   * Key: Tokenization ID <br>
   * Value: Map[Integer, Token] that represents [ID, Token] for this {@link Tokenization} <br>
   * <br>
   * Nested Map: <br>
   * Key: Token Sequence ID <br>
   * Value: {@link Token} object
   * 
   */
  private Map<UUID, Map<Integer, Token>> tokenizationIdToTokenSeqIdToTokensMap() {
    Map<UUID, Map<Integer, Token>> toRet = new HashMap<>();    
    for (Tokenization t : this.tokenizationIdToTokenizationMap.values()) {
      UUID tId = t.getUuid();
      Map<Integer, Token> idToTokenMap = new HashMap<Integer, Token>();
      if (t.isSetTokenList())
        for (Token tok : t.getTokenList().getTokenList()) {
          idToTokenMap.put(tok.getTokenIndex(), tok);
          toRet.put(tId, idToTokenMap);
        }
    }
    
    return toRet;
  }
  
  public final Map<UUID, Tokenization> getTokenizationIdToTokenizationMap() {
    return new HashMap<>(this.tokenizationIdToTokenizationMap);
  }
  
  public final Map<UUID, Map<Integer, Token>> getTokenizationIdToTokenIdxToTokenMap() {
    return new HashMap<>(this.tokenizationIdToTokenIdxToTokenMap);
  }
  
  public final Set<UUID> getTokenizationIds() {
    return new HashSet<>(this.tokenizationIdToTokenizationMap.keySet());
  }
  
  public Set<String> enumerateConstituentTags() {
    Set<String> dps = new HashSet<>();
    Collection<Tokenization> sectList = this.getTokenizationIdToTokenizationMap().values();
    for (Tokenization s : sectList)
      for (Parse p : s.getParseList())
        for (Constituent tt : p.getConstituentList())
          dps.add(tt.getTag());

    return dps;
  }
  
  public Set<String> enumerateDependencyParseTags() {
    Set<String> dps = new HashSet<>();
    Collection<Tokenization> sectList = this.getTokenizationIdToTokenizationMap().values();
    for (Tokenization s : sectList)
      for (DependencyParse dp : s.getDependencyParseList())
        for (Dependency d : dp.getDependencyList())
          dps.add(d.getEdgeType());

    return dps;
  }

  public Set<String> enumeratePartOfSpeechTags() {
//    Set<String> dps = new HashSet<>();
//    Collection<Tokenization> sectList = this.getTokenizationIdToTokenizationMap().values();
//    for (Tokenization s : sectList)
//      for (TaggedToken tt : s.getPosTagList().getTaggedTokenList())
//        dps.add(tt.getTag());
//
//    return dps;
    throw new UnsupportedOperationException("Method temporarily unsupported.");
  }

  public Set<String> enumerateNamedEntityTags() {
//    Set<String> dps = new HashSet<>();
//    Collection<Tokenization> sectList = this.getTokenizationIdToTokenizationMap().values();
//    for (Tokenization s : sectList)
//      for (TaggedToken tt : s.getNerTagList().getTaggedTokenList())
//        dps.add(tt.getTag());
//
//    return dps;
    throw new UnsupportedOperationException("Method temporarily unsupported.");
  }
}
