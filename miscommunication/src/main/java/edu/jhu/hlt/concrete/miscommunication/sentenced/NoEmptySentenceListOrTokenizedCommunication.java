/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sentenced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.sectioned.CachedSectionedCommunication;

/**
 * Implementation of {@link MappedSentenceCommunication} that optionally allows {@link Section}s
 * with list of {@link Sentence}s. It requires that each sentence list, if set, have at least one
 * sentence object inside the list, and that this sentence object does not have a {@link Tokenization}
 * set.
 */
public class NoEmptySentenceListOrTokenizedCommunication implements MappedSentenceCommunication {

  private final CachedSectionedCommunication csc;
  private final Map<UUID, Sentence> uuidToSentenceMap;

  /**
   *
   */
  public NoEmptySentenceListOrTokenizedCommunication(final Communication orig) throws MiscommunicationException {
    this.csc = new CachedSectionedCommunication(orig);

    List<Section> badSects = csc.getSections().stream()
        .filter(s -> !notSetOrNoEmptyTokenized(s))
        .collect(Collectors.toList());

    if (badSects.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("At least one section had either an empty Sentence list, or a Sentence with a Tokenization.\n");
      sb.append("Erroneous Sections:\n");
      badSects.forEach(sect -> {
        sb.append(sect.getUuid().getUuidString());
        sb.append("\n");
      });

      throw new MiscommunicationException(sb.toString());
    }

    final Map<UUID, Sentence> uuidToSentMap = new HashMap<>();
    this.csc.getSections().stream().filter(st -> st.isSetSentenceList())
        .flatMap(sect -> sect.getSentenceList().stream())
        .forEach(sent -> uuidToSentMap.put(sent.getUuid(), sent));
    this.uuidToSentenceMap = uuidToSentMap;
  }


  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sentenced.SentencedCommunication#getSentences()
   */
  @Override
  public List<Sentence> getSentences() {
    return new ArrayList<>(this.uuidToSentenceMap.values());
  }

  /**
   * @param stList a {@link List} of {@link Sentence} objects
   * @return true if any {@link Sentence} contains a set {@link Tokenization}
   */
  private static final boolean anyTokenized(final List<Sentence> stList) {
    return stList.stream().anyMatch(st -> st.isSetTokenization());
  }

  /**
   * @param st
   *          a {@link Section} to check for local properness
   * @return <code>true</code> if both the sentence list is unset, or the sentence list is set, has at least one {@link Sentence} object in it, and none of said
   *         Sentence objects have a {@link Tokenization} set.
   */
  private static final boolean notSetOrNoEmptyTokenized(final Section st) {
    return !st.isSetSentenceList()
        || (st.getSentenceListSize() > 0 && !anyTokenized(st.getSentenceList()));
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
    return new HashMap<>(this.uuidToSentenceMap);
  }
}
