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

public class IndexedSection extends IndexedProto<Concrete.Section> {
  private final FieldDescriptor SENTENCE_SEGMENTATION_FIELD = Concrete.Section.getDescriptor().findFieldByName("sentence_segmentation");

  public static IndexedSection build(Concrete.UUID uuid, ProtoIndex index) throws ConcreteException {
    IndexedSection cached = index.getIndexedProto(uuid);
    if (cached != null)
      return cached;
    else
      return new IndexedSection((Concrete.Section) index.lookup(uuid), index);
  }

  public static IndexedSection build(Concrete.Section section, ProtoIndex index) throws ConcreteException {
    IndexedSection cached = index.getIndexedProto(section.getUuid());
    if (cached != null)
      return cached;
    else
      return new IndexedSection(section, index);

  }

  private IndexedSection(Concrete.Section section, ProtoIndex index) throws ConcreteException {
    super(section, index);
  }

  // ======================================================================
  // SentenceSegmentations
  // ======================================================================

  /** Add a new SentenceSegmentation to a given Section in this communication. */
  public void addSentenceSegmentation(Concrete.SentenceSegmentation segmentation) throws ConcreteException {
    addField(SENTENCE_SEGMENTATION_FIELD, segmentation);
  }

  int getSentenceSegmentationCount() {
    return getProto().getSentenceSegmentationCount();
  }

  /**
   * Return an IndexedSentenceSegmentation for the sentence segmentation with the given index.
   */
  public IndexedSentenceSegmentation getSentenceSegmentation(int index) throws ConcreteException {
    return IndexedSentenceSegmentation.build(getProto().getSentenceSegmentation(index), getIndex());
  }

  /**
   * Return a list of IndexedSentenceSegmentations for the sentence segmentations in this section.
   */
  public List<IndexedSentenceSegmentation> getSentenceSegmentationList() throws ConcreteException {
    List<IndexedSentenceSegmentation> result = new ArrayList<IndexedSentenceSegmentation>();
    for (Concrete.SentenceSegmentation seg : getProto().getSentenceSegmentationList())
      result.add(IndexedSentenceSegmentation.build(seg, getIndex()));
    return result;
  }

  /**
   * Return an IndexedSentenceSegmentation for the unique sentence segmentation for this section. If this section has no sentence segmentation, or has multiple
   * sentence segmentations, then throw an exception.
   */
  public IndexedSentenceSegmentation getSentenceSegmentation() throws ConcreteException {
    if (getProto().getSentenceSegmentationCount() == 1)
      return getSentenceSegmentation(0);
    else if (getProto().getSentenceSegmentationCount() == 0)
      throw new ConcreteException("This section has no sentence segmentation");
    else
      throw new ConcreteException("This section has multiple sentence segmentations");
  }

  // ======================================================================
  // Sentences
  // ======================================================================

  /**
   * Return a list of all IndexedSentences for the sentences in this communication, using the unique sentence segmentations. If this communication does not have
   * a unique sentence segmentation, then throw an exception.
   */
  public List<IndexedSentence> getSentences() throws ConcreteException {
    return getSentenceSegmentation().getSentenceList();
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
