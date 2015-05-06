/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sentenced;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.sectioned.CachedSectionedCommunication;
import edu.jhu.hlt.concrete.miscommunication.sectioned.MappedSectionCommunication;

/**
 * Aggressively cached implementation of {@link MappedSentenceCommunication} and {@link MappedSectionCommunication}.
 * <br><br>
 * Assumes that each {@link Section} object has at least one {@link Sentence} object. If not, will throw a
 * {@link MiscommunicationException}.
 */
public class CachedSentencedCommunication implements MappedSentenceCommunication, MappedSectionCommunication {

  private final MappedSectionCommunication cpy;
  private final Map<UUID, Sentence> sentIdToSentenceMap;
  
  public CachedSentencedCommunication(final Communication orig) throws MiscommunicationException {
    this.cpy = new CachedSectionedCommunication(orig);
    List<Section> sectList = this.cpy.getSections();
    Optional<Section> bs = sectList.stream()
        .filter(s -> !validSentencePredicate(s))
        .findAny();
    if (bs.isPresent())
      throw new MiscommunicationException("At least one Section did not have Sentences (UUID = " + bs.get().getUuid().getUuidString() + ").");
    
    final Map<UUID, Sentence> toRet = new LinkedHashMap<>();
    for (Section s : sectList)
      for (Sentence st : s.getSentenceList())
        toRet.put(st.getUuid(), st);

    this.sentIdToSentenceMap = toRet;
  }
  
  private final boolean validSentencePredicate(final Section s) {
    return s.isSetSentenceList() && s.getSentenceListSize() > 0;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sentenced.SentencedCommunication#getSentences()
   */
  @Override
  public List<Sentence> getSentences() {
    return new ArrayList<>(this.sentIdToSentenceMap.values());
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.SectionedCommunication#getSections()
   */
  @Override
  public List<Section> getSections() {
    return this.cpy.getSections();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.WrappedCommunication#getRoot()
   */
  @Override
  public Communication getRoot() {
    return this.cpy.getRoot();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sentenced.MappedSentenceCommunication#getUuidToSentenceMap()
   */
  @Override
  public Map<UUID, Sentence> getUuidToSentenceMap() {
    return new LinkedHashMap<>(this.sentIdToSentenceMap);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.MappedSectionCommunication#getUuidToSectionMap()
   */
  @Override
  public Map<UUID, Section> getUuidToSectionMap() {
    return this.cpy.getUuidToSectionMap();
  }
}
