/*
 * 
 */
package edu.jhu.hlt.concrete.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Token;

/**
 * Aggressively cached version of {@link SuperCommunication}.
 * 
 * @author max
 */
public class CachedSuperCommunication extends SuperCommunication {

  protected final Map<String, Section> sectIdToSectMap;
  protected final Map<String, Sentence> sentIdToSentMap;
  protected final Map<String, Map<Integer, Token>> tokenizationIdToTokenIdToTokenMap;
  
  /**
   * @param comm
   */
  public CachedSuperCommunication(Communication comm) {
    super(comm);
    this.sectIdToSectMap = super.sectionIdToSectionMap();
    this.sentIdToSentMap = super.sentIdToSentenceMap();
    this.tokenizationIdToTokenIdToTokenMap = super.tokenizationIdToTokenSeqIdToTokensMap();
  }
  
  @Override
  public final Map<String, Section> sectionIdToSectionMap() {
    return new HashMap<>(this.sectIdToSectMap);
  }
  
  @Override
  public final Map<String, Sentence> sentIdToSentenceMap() {
    return new HashMap<>(this.sentIdToSentMap);
  }
  
  @Override
  public final Map<String, Map<Integer, Token>> tokenizationIdToTokenSeqIdToTokensMap() {
    return new HashMap<>(this.tokenizationIdToTokenIdToTokenMap);
  }
  
  public final Set<String> getSectionIds() {
    return new HashSet<>(this.sectIdToSectMap.keySet());
  }
  
  public final Set<String> getSentenceIds() {
    return new HashSet<>(this.sentIdToSentMap.keySet());
  }
  
  public final Set<String> getTokenizationIds() {
    return new HashSet<>(this.tokenizationIdToTokenIdToTokenMap.keySet());
  }
}
