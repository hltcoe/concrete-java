/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.entitied;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.tokenized.CachedTokenizationCommunication;

/**
 *
 */
public class CachedEntityMentionedCommunication implements EntityMentionedCommunication {

  private final CachedTokenizationCommunication ctc;
  private List<EntityMention> emsList;

  /**
   *
   */
  public CachedEntityMentionedCommunication(final Communication c) throws MiscommunicationException {
    this.ctc = new CachedTokenizationCommunication(c);
    this.emsList = new ArrayList<>();
    c.getEntityMentionSetList().forEach(sms -> this.emsList.addAll(sms.getMentionList()));
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication#getTokenizations()
   */
  @Override
  public List<Tokenization> getTokenizations() {
    return this.ctc.getTokenizations();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sentenced.SentencedCommunication#getSentences()
   */
  @Override
  public List<Sentence> getSentences() {
    return this.ctc.getSentences();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.SectionedCommunication#getSections()
   */
  @Override
  public List<Section> getSections() {
    return this.ctc.getSections();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.WrappedCommunication#getRoot()
   */
  @Override
  public Communication getRoot() {
    return this.ctc.getRoot();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.entitied.EntityMentionedCommunication#getEntityMentions()
   */
  @Override
  public List<EntityMention> getEntityMentions() {
    return new ArrayList<>(this.emsList);
  }
}
