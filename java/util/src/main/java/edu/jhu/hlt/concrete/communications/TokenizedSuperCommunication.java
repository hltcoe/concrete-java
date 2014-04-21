/*
 * 
 */
package edu.jhu.hlt.concrete.communications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;

/**
 * @author max
 *
 */
public class TokenizedSuperCommunication extends SentencedSuperCommunication {

  protected final Map<String, Tokenization> tokenizationIdToTokenizationMap;
  protected final Map<String, Map<Integer, Token>> tokenizationIdToTokenIdxToTokenMap;
  
  /**
   * @param comm
   */
  public TokenizedSuperCommunication(Communication comm) {
    super(comm);
    this.tokenizationIdToTokenizationMap = this.tokenizationIdToTokenizationMap();
    this.tokenizationIdToTokenIdxToTokenMap = this.tokenizationIdToTokenSeqIdToTokensMap();
  }

  private final Map<String, Tokenization> tokenizationIdToTokenizationMap() {
    final Map<String, Tokenization> toRet = new HashMap<>();
    List<Sentence> stList = new ArrayList<>(this.sentIdToSentenceMap.values());
    for (Sentence st : stList)
      if (st.isSetTokenizationList())
        for (Tokenization t : st.getTokenizationList()) {
          String tId = t.getUuid();
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
  private Map<String, Map<Integer, Token>> tokenizationIdToTokenSeqIdToTokensMap() {
    Map<String, Map<Integer, Token>> toRet = new HashMap<>();    
    for (Tokenization t : this.tokenizationIdToTokenizationMap.values()) {
      String tId = t.getUuid();
      Map<Integer, Token> idToTokenMap = new HashMap<Integer, Token>();
      if (t.isSetTokenList())
        for (Token tok : t.getTokenList()) {
          idToTokenMap.put(tok.getTokenIndex(), tok);
          toRet.put(tId, idToTokenMap);
        }
    }
    
    return toRet;
  }
  
  public final Map<String, Tokenization> getTokenizationIdToTokenizationMap() {
    return new HashMap<>(this.tokenizationIdToTokenizationMap);
  }
  
  public final Map<String, Map<Integer, Token>> getTokenizationIdToTokenIdxToTokenMap() {
    return new HashMap<>(this.tokenizationIdToTokenIdxToTokenMap);
  }
  
  public final Set<String> getTokenizationIds() {
    return new HashSet<>(this.tokenizationIdToTokenizationMap.keySet());
  }
}
