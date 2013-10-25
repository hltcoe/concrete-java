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

public class IndexedSectionSegmentation extends IndexedProto<Concrete.SectionSegmentation> {

  public static IndexedSectionSegmentation build(Concrete.UUID uuid, ProtoIndex index) throws ConcreteException {
    IndexedSectionSegmentation cached = index.getIndexedProto(uuid);
    if (cached != null)
      return cached;
    else
      return new IndexedSectionSegmentation((Concrete.SectionSegmentation) index.lookup(uuid), index);
  }

  public static IndexedSectionSegmentation build(Concrete.SectionSegmentation sectionSeg, ProtoIndex index) throws ConcreteException {
    IndexedSectionSegmentation cached = index.getIndexedProto(sectionSeg.getUuid());
    if (cached != null)
      return cached;
    else
      return new IndexedSectionSegmentation(sectionSeg, index);

  }

  private IndexedSectionSegmentation(Concrete.SectionSegmentation sectionSeg, ProtoIndex index) throws ConcreteException {
    super(sectionSeg, index);
  }

  // ======================================================================
  // Sections
  // ======================================================================

  int getSectionCount() {
    return getProto().getSectionCount();
  }

  /**
   * Return an IndexedSection for the section with the given index.
   */
  public IndexedSection getSection(int index) throws ConcreteException {
    return IndexedSection.build(getProto().getSection(index), getIndex());
  }

  /**
   * Return a list of IndexedSections for the sections in this section segmentation.
   */
  public List<IndexedSection> getSectionList() throws ConcreteException {
    List<IndexedSection> result = new ArrayList<IndexedSection>();
    for (Concrete.Section seg : getProto().getSectionList())
      result.add(IndexedSection.build(seg, getIndex()));
    return result;
  }
}
