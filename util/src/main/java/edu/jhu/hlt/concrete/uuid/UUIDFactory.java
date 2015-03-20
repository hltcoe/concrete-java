/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.uuid;

import edu.jhu.hlt.concrete.UUID;

/**
 * A class that offers Concrete {@link UUID} utilities.
 */
public class UUIDFactory {

  private UUIDFactory() {

  }

  /**
   * @return a new Concrete {@link UUID} that wraps a Java {@link java.util.UUID}.
   */
  public static UUID newUUID() {
    return new UUID(java.util.UUID.randomUUID().toString());
  }
  
  /**
   * @param uuid a {@link java.util.UUID}
   * @return a Concrete {@link UUID}
   */
  public static UUID fromJavaUUID(java.util.UUID uuid) {
    return new UUID(uuid.toString());
  }

  /**
   * Guess, then determine, if a string is truly a {@link UUID} string.
   *
   * Unfortunately, an exception will be thrown if it is not valid.
   *
   * @param uuidStr a {@link String} representing a UUID to check
   * @return true if a valid {@link java.util.UUID} string, otherwise false.
   */
  public static boolean isValidUUID(String uuidStr) {
    // Do what is possible to avoid an exception.
    if (uuidStr != null && uuidStr.length() == 36 && uuidStr.contains("-"))
      // Could do more above, but just eat the exception
      // if it's invalid
      try {
        java.util.UUID.fromString(uuidStr);
        return true;
      } catch (IllegalArgumentException iae) {
        return false;
      }
    else
      return false;
  }
}
