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
import java.util.UUID;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SentenceSegmentation;

/**
 * @author max
 *
 */
public class SentencedSuperCommunication extends SectionedSuperCommunication {

  protected final Map<String, Sentence> sentIdToSentenceMap;
  
  /**
   * @param comm
   */
  public SentencedSuperCommunication(Communication comm) {
    super(comm);
    this.sentIdToSentenceMap = this.sentIdToSentenceMap();
  }

  
  /**
   * Return a {@link Map} of [SentenceID, Sentence] for all {@link SentenceSegmentation}s in all {@link SectionSegmentation}s.
   * 
   * @return a {@link Map} whose keys contain {@link Sentence} {@link UUID} strings, and whose values contain {@link Section} objects with that id string.
   */
  private final Map<String, Sentence> sentIdToSentenceMap() {
    final Map<String, Sentence> toRet = new HashMap<String, Sentence>();

    List<Section> sectList = new ArrayList<>(this.sectionIdToSectionMap.values());
    for (Section s : sectList)
      if (s.isSetSentenceSegmentation())
        for (SentenceSegmentation ss : s.getSentenceSegmentation())
          if (ss.isSetSentenceList())
            for (Sentence st : ss.getSentenceList())
              toRet.put(st.getUuid(), st);

    return toRet;
  }
  
  public final Map<String, Sentence> getSentenceIdToSentenceMap() {
    return new HashMap<>(this.sentIdToSentenceMap);
  }
  
  public final Set<String> getSentenceIds() {
    return new HashSet<>(this.sentIdToSentenceMap.keySet());
  }
}
