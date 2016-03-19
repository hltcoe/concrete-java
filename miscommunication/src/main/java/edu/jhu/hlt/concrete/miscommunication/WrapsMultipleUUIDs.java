/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication;

import java.util.Set;

import edu.jhu.hlt.concrete.UUID;

/**
 * An interface that can be used to provide utility for
 * classes with multiple {@link UUID} objects.
 */
public interface WrapsMultipleUUIDs {
  /**
   * This method should return all {@link UUID} objects that
   * the implementation has underneath it.
   * <br><br>
   * The type, thread-safety, etc. of the underlying returned {@link Set}
   * is not guaranteed.
   *
   * @return a {@link Set} of {@link UUID} objects across this object
   */
  public Set<UUID> getUUIDSet();
}
