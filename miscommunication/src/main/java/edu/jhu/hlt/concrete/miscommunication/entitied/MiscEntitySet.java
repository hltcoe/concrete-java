/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.entitied;

import java.util.HashSet;
import java.util.Set;

import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.WrapsMultipleUUIDs;

/**
 *
 */
public class MiscEntitySet implements WrapsMultipleUUIDs {

  private final Set<String> uuidSet = new HashSet<>();

  /**
   *
   */
  public MiscEntitySet(final EntitySet es) throws MiscommunicationException {
    this.uuidSet.add(es.getUuid().getUuidString());
    // check mention set ID if set
    if (es.isSetMentionSetId())
      if (!this.uuidSet.add(es.getMentionSetId().getUuidString()))
        throw new MiscommunicationException("EntitySet and mentionSetId UUIDs are the same.");

    // check Linkings if set
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.miscommunication.WrapsMultipleUUIDs#getUUIDSet()
   */
  @Override
  public Set<UUID> getUUIDSet() {
    // TODO Auto-generated method stub
    return null;
  }

}
