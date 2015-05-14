/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sectioned;

import java.util.Map;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.UUID;

/**
 * Interface allowing a consumer to retrieve a {@link Map} of {@link UUID}
 * to {@link Section} objects for easy UUID-based lookup.
 */
public interface MappedSectionCommunication extends SectionedCommunication {
  public Map<UUID, Section> getUuidToSectionMap();
}
