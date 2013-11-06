/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.index;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.ConcreteException;

public class IndexedSentenceSegmentation extends IndexedProto<Concrete.SentenceSegmentation> {

  public static IndexedSentenceSegmentation build(Concrete.UUID uuid, ProtoIndex index) throws ConcreteException {
    IndexedSentenceSegmentation cached = index.getIndexedProto(uuid);
    if (cached != null)
      return cached;
    else
      return new IndexedSentenceSegmentation((Concrete.SentenceSegmentation) index.lookup(uuid), index);
  }

  public static IndexedSentenceSegmentation build(Concrete.SentenceSegmentation sentSeg, ProtoIndex index) throws ConcreteException {
    IndexedSentenceSegmentation cached = index.getIndexedProto(sentSeg.getUuid());
    if (cached != null)
      return cached;
    else
      return new IndexedSentenceSegmentation(sentSeg, index);

  }

  private IndexedSentenceSegmentation(Concrete.SentenceSegmentation sentSeg, ProtoIndex index) throws ConcreteException {
    super(sentSeg, index);
  }

  // ======================================================================
  // Sentences
  // ======================================================================

  int getSentenceCount() {
    return getProto().getSentenceCount();
  }

  /**
   * Return an IndexedSentence for the sentence with the given index.
   */
  public IndexedSentence getSentence(int index) throws ConcreteException {
    return IndexedSentence.build(getProto().getSentence(index), getIndex());
  }

  /**
   * Return a list of IndexedSentences for the sentences in this sentence segmentation.
   */
  public List<IndexedSentence> getSentenceList() throws ConcreteException {
    List<IndexedSentence> result = new ArrayList<IndexedSentence>();
    for (Concrete.Sentence seg : getProto().getSentenceList())
      result.add(IndexedSentence.build(seg, getIndex()));
    return result;
  }
}
