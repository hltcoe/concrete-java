/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.index;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Descriptors.FieldDescriptor;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.ConcreteException;

public class IndexedSentence extends IndexedProto<Concrete.Sentence> {
  private final FieldDescriptor TOKENIZATION_FIELD = Concrete.Sentence.getDescriptor().findFieldByName("tokenization");

  public static IndexedSentence build(Concrete.UUID uuid, ProtoIndex index) throws ConcreteException {
    IndexedSentence cached = index.getIndexedProto(uuid);
    if (cached != null)
      return cached;
    else
      return new IndexedSentence((Concrete.Sentence) index.lookup(uuid), index);
  }

  public static IndexedSentence build(Concrete.Sentence sentence, ProtoIndex index) throws ConcreteException {
    IndexedSentence cached = index.getIndexedProto(sentence.getUuid());
    if (cached != null)
      return cached;
    else
      return new IndexedSentence(sentence, index);

  }

  private IndexedSentence(Concrete.Sentence sentence, ProtoIndex index) throws ConcreteException {
    super(sentence, index);
  }

  // ======================================================================
  // Tokenizations
  // ======================================================================

  /** Add a new Tokenization to a given Sentence in this communication. */
  public void addTokenization(Concrete.Tokenization tokenization) throws ConcreteException {
    addField(TOKENIZATION_FIELD, tokenization);
  }

  int getTokenizationCount() {
    return getProto().getTokenizationCount();
  }

  /**
   * Return an IndexedTokenization for the tokenization with the given index.
   */
  public IndexedTokenization getTokenization(int index) throws ConcreteException {
    return IndexedTokenization.build(getProto().getTokenization(index), getIndex());
  }

  /**
   * Return a list of IndexedTokenizations for the tokenizations in this sentence.
   */
  public List<IndexedTokenization> getTokenizationList() throws ConcreteException {
    List<IndexedTokenization> result = new ArrayList<IndexedTokenization>();
    for (Concrete.Tokenization tokz : getProto().getTokenizationList())
      result.add(IndexedTokenization.build(tokz, getIndex()));
    return result;
  }

  /**
   * Return an IndexedTokenization for the unique tokenization for this sentence. If this sentence has no tokenization, or has multiple tokenizations, then
   * throw an exception.
   */
  public IndexedTokenization getTokenization() throws ConcreteException {
    if (getProto().getTokenizationCount() == 1)
      return getTokenization(0);
    else if (getProto().getTokenizationCount() == 0)
      throw new ConcreteException("This sentence has no tokenization");
    else
      throw new ConcreteException("This sentence has multiple tokenizations");
  }

  // ======================================================================
  // Other Attributes
  // ======================================================================

  public Concrete.TextSpan getTextSpan() {
    return getProto().getTextSpan();
  }

  public Concrete.AudioSpan getAudioSpan() {
    return getProto().getAudioSpan();
  }

}
