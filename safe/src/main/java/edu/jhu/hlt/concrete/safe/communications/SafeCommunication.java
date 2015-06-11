/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.safe.communications;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.safe.metadata.Metadatable;
import edu.jhu.hlt.utilt.uuid.UUIDable;

/**
 * Interface mirroring {@link Communication} that, when converted to a Communication,
 * is guaranteed to be serializable without errors due to missing required fields.
 */
public interface SafeCommunication extends UUIDable, Metadatable {
  public String getId();

  public String getType();
}
