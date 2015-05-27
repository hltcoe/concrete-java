/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sentenced;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;

/**
 * Implementation of {@link MappedSentenceCommunication} that requires each {@link Sentence}
 * has no {@link Tokenization} set.
 */
public class NonTokenizedSentencedCommunication implements MappedSentenceCommunication {

  private final CachedSentencedCommunication csc;

  /**
   *
   */
  public NonTokenizedSentencedCommunication(final Communication orig) throws MiscommunicationException {
    this.csc = new CachedSentencedCommunication(orig);

    final Map<UUID, Sentence> idToSentMap = this.csc.getUuidToSentenceMap();
    final Map<UUID, Sentence> badSents = new HashMap<>();

    idToSentMap.entrySet().stream().filter(e -> e.getValue().isSetTokenization())
        .forEach(e -> badSents.put(e.getKey(), e.getValue()));

    if (badSents.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("This object requires that no tokenizations be set. The following Sentences have tokenizations set: \n");
      idToSentMap.entrySet().forEach(e -> {
        final UUID k = e.getKey();
        sb.append("Sentence: ");
        sb.append(k.getUuidString());
        sb.append("\n");
      });

      throw new MiscommunicationException(sb.toString());
    }
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sentenced.SentencedCommunication#getSentences()
   */
  @Override
  public List<Sentence> getSentences() {
    return this.csc.getSentences();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.SectionedCommunication#getSections()
   */
  @Override
  public List<Section> getSections() {
    return this.csc.getSections();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.WrappedCommunication#getRoot()
   */
  @Override
  public Communication getRoot() {
    return this.csc.getRoot();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sentenced.MappedSentenceCommunication#getUuidToSentenceMap()
   */
  @Override
  public Map<UUID, Sentence> getUuidToSentenceMap() {
    return this.csc.getUuidToSentenceMap();
  }
}
