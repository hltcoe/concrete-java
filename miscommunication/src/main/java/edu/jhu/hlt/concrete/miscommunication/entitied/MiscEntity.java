/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.entitied;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.WrapsMultipleUUIDs;

/**
 *
 */
public class MiscEntity implements WrapsMultipleUUIDs {

  private final Set<String> uuidStringSet;

  /**
   * @throws MiscommunicationException on invalid {@link Entity} (duplicate UUIDs)
   */
  public MiscEntity(final Entity e) throws MiscommunicationException {
    List<String> strs = e.getMentionIdList().stream()
      .map(UUID::getUuidString)
      .collect(Collectors.toList());
    this.uuidStringSet = new HashSet<>(strs.size() + 1);
    this.uuidStringSet.add(e.getUuid().getUuidString());
    for (String s : strs)
      if (!this.uuidStringSet.add(s))
        throw new MiscommunicationException("Entity contains at least one duplicate UUID: " + s);
  }

  @Override
  public Set<UUID> getUUIDSet() {
    return new HashSet<>(this.uuidStringSet)
        .stream()
        .map(UUID::new)
        .collect(Collectors.toSet());
  }
}
