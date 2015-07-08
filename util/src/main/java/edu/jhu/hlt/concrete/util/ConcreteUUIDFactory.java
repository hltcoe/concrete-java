/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * See {@link UUIDFactory}.
 *
 * @see UUIDFactory
 * @deprecated
 */
@Deprecated
public class ConcreteUUIDFactory {

  /**
   *
   */
  public ConcreteUUIDFactory() {
    // TODO Auto-generated constructor stub
  }

  public UUID getConcreteUUID() {
    return new UUID(java.util.UUID.randomUUID().toString());
  }

  public UUID convertUUIDString(String uuidString) throws InvalidUUIDException {
    if (Util.isValidUUIDString(uuidString))
      return new UUID(uuidString);
    else
      throw new InvalidUUIDException("Not a UUID: " + uuidString);
  }

  public java.util.UUID concreteToJavaUUID(UUID uuid) {
    return java.util.UUID.fromString(uuid.getUuidString());
  }
}
