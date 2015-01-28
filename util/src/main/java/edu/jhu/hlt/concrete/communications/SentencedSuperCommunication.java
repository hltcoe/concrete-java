/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.communications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.UUID;

/**
 * @author max
 *
 */
public class SentencedSuperCommunication extends SectionedSuperCommunication {

  protected final Map<UUID, Sentence> sentIdToSentenceMap;
  
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
  private final Map<UUID, Sentence> sentIdToSentenceMap() {
    final Map<UUID, Sentence> toRet = new HashMap<>();

    List<Section> sectList = new ArrayList<>(this.sectionIdToSectionMap.values());
    for (Section s : sectList)
      for (Sentence st : s.getSentenceList())
        toRet.put(st.getUuid(), st);

    return toRet;
  }
  
  public final Map<UUID, Sentence> getSentenceIdToSentenceMap() {
    return new HashMap<>(this.sentIdToSentenceMap);
  }
  
  public final Set<UUID> getSentenceIds() {
    return new HashSet<>(this.sentIdToSentenceMap.keySet());
  }
}
