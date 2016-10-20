/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.UUID;

/**
 * Interface whose implementations have a {@link UUID} associated with them.
 */
public interface UUIDable {
  public UUID getUUID();
}
