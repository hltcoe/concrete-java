/*
 * 
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
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;

/**
 * @author max
 *
 */
public class TokenizedSuperCommunication extends SentencedSuperCommunication {

  protected final Map<UUID, Tokenization> tokenizationIdToTokenizationMap;
  protected final Map<UUID, Map<Integer, Token>> tokenizationIdToTokenIdxToTokenMap;
  
  /**
   * @param comm
   */
  public TokenizedSuperCommunication(Communication comm) {
    super(comm);
    this.tokenizationIdToTokenizationMap = this.tokenizationIdToTokenizationMap();
    this.tokenizationIdToTokenIdxToTokenMap = this.tokenizationIdToTokenSeqIdToTokensMap();
  }

  private final Map<UUID, Tokenization> tokenizationIdToTokenizationMap() {
    final Map<UUID, Tokenization> toRet = new HashMap<>();
    List<Sentence> stList = new ArrayList<>(this.sentIdToSentenceMap.values());
    for (Sentence st : stList)
      if (st.isSetTokenizationList())
        for (Tokenization t : st.getTokenizationList()) {
          UUID tId = t.getUuid();
          toRet.put(tId, t);
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
        for (Token tok : t.getTokenList()) {
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
      for (Constituent tt : s.getParse().getConstituentList())
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
    Set<String> dps = new HashSet<>();
    Collection<Tokenization> sectList = this.getTokenizationIdToTokenizationMap().values();
    for (Tokenization s : sectList)
      for (TaggedToken tt : s.getPosTagList().getTaggedTokenList())
        dps.add(tt.getTag());

    return dps;
  }

  public Set<String> enumerateNamedEntityTags() {
    Set<String> dps = new HashSet<>();
    Collection<Tokenization> sectList = this.getTokenizationIdToTokenizationMap().values();
    for (Tokenization s : sectList)
      for (TaggedToken tt : s.getNerTagList().getTaggedTokenList())
        dps.add(tt.getTag());

    return dps;
  }
}
