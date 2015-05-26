/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sectioned;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;

/**
 * Implementation of {@link MappedSectionCommunication} requiring that each {@link Section}
 * has no {@link Sentence}s.
 */
public class NonSentencedSectionedCommunication implements MappedSectionCommunication {

  private final CachedSectionedCommunication csc;

  /**
   *
   * @param orig
   *          the {@link Communication} to wrap
   * @throws MiscommunicationException
   *           if no {@link Section}s are present, or sections's {@link Section#isSetSentenceList()}
   *           method is <code>true</code>
   */
  public NonSentencedSectionedCommunication(final Communication orig) throws MiscommunicationException {
    this.csc = new CachedSectionedCommunication(orig);
    Map<UUID, Section> badSections = new HashMap<>();
    Map<UUID, Section> idToSectMap = csc.getUuidToSectionMap();
    idToSectMap.entrySet().stream().filter(e -> {
      return e.getValue().isSetSentenceList();
    })
    .forEach(e -> badSections.put(e.getKey(), e.getValue()));

    if (badSections.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("This object requires that no sentenceLists be set. The following Sections have sentenceLists set: \n");
      idToSectMap.entrySet().forEach(e -> {
        final UUID k = e.getKey();
        sb.append("Section: ");
        sb.append(k.getUuidString());
        sb.append("\n");
      });

      throw new MiscommunicationException(sb.toString());
    }
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.WrappedCommunication#getRoot()
   */
  @Override
  public Communication getRoot() {
    return this.csc.getRoot();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.SectionedCommunication#getSections()
   */
  @Override
  public List<Section> getSections() {
    return this.csc.getSections();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.sectioned.MappedSectionCommunication#getUuidToSectionMap()
   */
  @Override
  public Map<UUID, Section> getUuidToSectionMap() {
    return this.csc.getUuidToSectionMap();
  }
}
