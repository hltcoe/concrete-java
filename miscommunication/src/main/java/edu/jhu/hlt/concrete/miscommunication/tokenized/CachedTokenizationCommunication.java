/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.tokenized;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.sectioned.MappedSectionCommunication;
import edu.jhu.hlt.concrete.miscommunication.sentenced.CachedSentencedCommunication;
import edu.jhu.hlt.concrete.miscommunication.sentenced.MappedSentenceCommunication;

/**
 * Aggressively cached implementation of {@link MappedTokenizedCommunication},
 * {@link MappedSentenceCommunication}, and {@link MappedSectionCommunication}.
 * <br><br>
 * Assumes that each {@link Sentence} object has at least one {@link Tokenization} object. If not, will throw a
 * {@link MiscommunicationException}.
 */
public class CachedTokenizationCommunication implements MappedTokenizedCommunication, MappedSentenceCommunication, MappedSectionCommunication {

  private final CachedSentencedCommunication cpy;

  private final Map<UUID, Tokenization> tokenizationIdToTokenizationMap;
  private final Map<UUID, Map<Integer, Token>> tokenizationIdToTokenIdxToTokenMap;

  public CachedTokenizationCommunication(final Communication orig) throws MiscommunicationException {
    this.cpy = new CachedSentencedCommunication(orig);
    Optional<Sentence> bs = this.cpy.getSentences().stream()
        .filter(s -> !validPredicate(s))
        .findAny();
    if (bs.isPresent())
      throw new MiscommunicationException("At least one Sentence did not have a Tokenization (UUID = " + bs.get().getUuid().getUuidString() + ").");

    final Map<UUID, Tokenization> toRet = new LinkedHashMap<>();
    final Map<UUID, Map<Integer, Token>> uuidToIdxToTokenMap = new LinkedHashMap<>();

    List<Sentence> stList = new ArrayList<>(this.cpy.getSentences());
    for (Sentence st : stList) {
      Tokenization tok = st.getTokenization();
      UUID tId = tok.getUuid();
      toRet.put(tId, tok);

      final Map<Integer, Token> idToTokenMap = new LinkedHashMap<>();
      if (tok.isSetTokenList())
        for (Token t: tok.getTokenList().getTokenList()) {
          idToTokenMap.put(t.getTokenIndex(), t);
          uuidToIdxToTokenMap.put(tId, idToTokenMap);
        }
    }

    this.tokenizationIdToTokenizationMap = toRet;
    this.tokenizationIdToTokenIdxToTokenMap = uuidToIdxToTokenMap;
  }

  private final boolean validPredicate(final Sentence s) {
    return s.isSetTokenization();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sentenced.SentencedCommunication#getSentences()
   */
  @Override
  public List<Sentence> getSentences() {
    return this.cpy.getSentences();
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
    return this.cpy.getUuidToSentenceMap();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.MappedSectionCommunication#getUuidToSectionMap()
   */
  @Override
  public Map<UUID, Section> getUuidToSectionMap() {
    return this.cpy.getUuidToSectionMap();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication#getTokenizations()
   */
  @Override
  public List<Tokenization> getTokenizations() {
    return new ArrayList<>(this.tokenizationIdToTokenizationMap.values());
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.tokenized.MappedTokenizedCommunication#getUuidToTokenizationMap()
   */
  @Override
  public Map<UUID, Tokenization> getUuidToTokenizationMap() {
    return new LinkedHashMap<>(this.tokenizationIdToTokenizationMap);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.tokenized.MappedTokenizedCommunication#getUuidToTokenIdxToTokenMap()
   */
  @Override
  public Map<UUID, Map<Integer, Token>> getUuidToTokenIdxToTokenMap() {
    return new LinkedHashMap<>(this.tokenizationIdToTokenIdxToTokenMap);
  }
}
