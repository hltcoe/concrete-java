/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.index;

/* Things I might want to add:

 - parent pointers -- e.g., given a tokenization, what sentence does
 it come from?  This may only be practical for objects that have
 uuids -- i.e., not for tokens or parse constituents??
 - complain vociferously about duplicate uuids
 - getTranslatedSentences()
 - parse tree navigation
 - get text for arbitrary(ish) element
 - from spans or from tokens

 - IndexedTokenization, IndexedParse, etc??

 */

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Descriptors.FieldDescriptor;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.ConcreteException;

/**
 * A wrapper for a Communication that tracks modifications and provides automatically built indices.
 */
public class IndexedCommunication extends IndexedProto<Concrete.Communication> {
  private final FieldDescriptor SECTION_SEGMENTATION_FIELD = Concrete.Communication.getDescriptor().findFieldByName("section_segmentation");
  private final FieldDescriptor LANGUAGE_ID_FIELD = Concrete.Communication.getDescriptor().findFieldByName("language_id");
  private final FieldDescriptor ENTITY_MENTION_SET_FIELD = Concrete.Communication.getDescriptor().findFieldByName("entity_mention_set");

  // ======================================================================
  // Constructor
  // ======================================================================

  public IndexedCommunication(Concrete.Communication comm, ProtoIndex index) throws ConcreteException {
    super(comm, index);
    if (comm != index.getRoot()) {
      // System.err.println("Comm="+comm);
      // System.err.println("root="+index.getRoot());
      throw new ConcreteException("Expected index root to be comm");
    }
  }

  // ======================================================================
  // Modification Convenience Methods
  // ======================================================================

  /** Add a new LanguageIdentification to this communication. */
  public void addLanguageId(Concrete.LanguageIdentification lid) throws ConcreteException {
    addField(LANGUAGE_ID_FIELD, lid);
  }

  public void addEntityMentionSet(Concrete.EntityMentionSet emset) throws ConcreteException {
    addField(ENTITY_MENTION_SET_FIELD, emset);
  }

  // ======================================================================
  // Section Segmentations
  // ======================================================================

  /** Add a new SectionSegmentation to this communication. */
  public void addSectionSegmentation(Concrete.SectionSegmentation segmentation) throws ConcreteException {
    addField(SECTION_SEGMENTATION_FIELD, segmentation);
  }

  int getSectionSegmentationCount() {
    return getProto().getSectionSegmentationCount();
  }

  /**
   * Return an IndexedSectionSegmentation for the section segmentation with the given index.
   */
  public IndexedSectionSegmentation getSectionSegmentation(int index) throws ConcreteException {
    return IndexedSectionSegmentation.build(getProto().getSectionSegmentation(index), getIndex());
  }

  /**
   * Return a list of IndexedSectionSegmentations for the section segmentations in this communication.
   */
  public List<IndexedSectionSegmentation> getSectionSegmentationList() throws ConcreteException {
    List<IndexedSectionSegmentation> result = new ArrayList<IndexedSectionSegmentation>();
    for (Concrete.SectionSegmentation seg : getProto().getSectionSegmentationList())
      result.add(IndexedSectionSegmentation.build(seg, getIndex()));
    return result;
  }

  /**
   * Return an IndexedSectionSegmentation for the unique section segmentation for this communication. If this communication has no section segmentation, or has
   * multiple section segmentations, then throw an exception.
   */
  public IndexedSectionSegmentation getSectionSegmentation() throws ConcreteException {
    if (getProto().getSectionSegmentationCount() == 1)
      return getSectionSegmentation(0);
    else if (getProto().getSectionSegmentationCount() == 0)
      throw new ConcreteException("This communication has no section segmentation");
    else
      throw new ConcreteException("This communication has multiple section segmentations");
  }

  // ======================================================================
  // Sentences
  // ======================================================================

  /**
   * Return a list of all IndexedSections for the sections in this communication, using the unique section segmentations. If this communication does not have a
   * unique section segmentation, then throw an exception.
   */
  public List<IndexedSection> getSections() throws ConcreteException {
    return getSectionSegmentation().getSectionList();
  }

  /**
   * Return a list of all IndexedSentences for the sentences in this communication, using the unique section segmentations and sentence segmentations. If this
   * communication does not have a unique section segmentation, or any section does not have a unique sentence segmentation, then throw an exception.
   */
  public List<IndexedSentence> getSentences() throws ConcreteException {
    List<IndexedSentence> result = new ArrayList<IndexedSentence>();
    for (IndexedSection sec : getSections())
      result.addAll(sec.getSentences());
    return result;
  }

  // ======================================================================
  // Other Accessors
  // ======================================================================

  public String getText() {
    return protoObj.getText();
  }

  public Concrete.CommunicationGUID getGuid() {
    return protoObj.getGuid();
  }

  public String getCommunicationId() {
    return protoObj.getGuid().getCommunicationId();
  }

  public String getCorpusName() {
    return protoObj.getGuid().getCorpusName();
  }

  // ======================================================================
  // Token/Tokenization Index Lookup
  // ======================================================================

  public Concrete.Token getToken(Concrete.TokenRef ref) throws ConcreteException {
    // return IndexedTokenization.build(ref.getTokenization(), getIndex()).getToken(ref.getTokenIndex());
    return IndexedTokenization.build(ref.getTokenizationId(), getIndex()).getToken(ref.getTokenIndex());
  }
}
