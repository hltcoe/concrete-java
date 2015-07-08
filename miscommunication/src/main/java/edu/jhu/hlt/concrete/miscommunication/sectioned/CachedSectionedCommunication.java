/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sectioned;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;

/**
 * Aggressively cached implementation of {@link MappedSectionCommunication}.
 */
public class CachedSectionedCommunication implements MappedSectionCommunication {

  private final Communication cpy;

  private final Map<UUID, Section> sectionIdToSectionMap;
  private final int nSections;

  /**
   * @param orig
   * @throws MiscommunicationException
   */
  public CachedSectionedCommunication(final Communication orig) throws MiscommunicationException {
    if (!orig.isSetSectionList() || orig.getSectionListSize() <= 0)
      throw new MiscommunicationException("Communication did not have sections set or there were zero sections.");
    this.cpy = new Communication(orig);

    final Map<UUID, Section> map = new LinkedHashMap<>();
    for (Section s : this.cpy.getSectionList())
      map.put(s.getUuid(), s);

    this.sectionIdToSectionMap = map;
    this.nSections = map.size();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.WrappedCommunication#getRoot()
   */
  @Override
  public Communication getRoot() {
    return new Communication(this.cpy);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.SectionedCommunication#getSections()
   */
  @Override
  public List<Section> getSections() {
    return new ArrayList<>(this.sectionIdToSectionMap.values());
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.MappedSectionCommunication#getUuidToSectionMap()
   */
  @Override
  public Map<UUID, Section> getUuidToSectionMap() {
    return new LinkedHashMap<>(this.sectionIdToSectionMap);
  }

  /**
   * @return the last {@link Section} in this {@link Communication}
   */
  public Section lastSection() {
    return this.getSections().get(this.nSections - 1);
  }
}
