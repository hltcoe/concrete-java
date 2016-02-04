/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import java.util.UUID;

/**
 * Will be removed in a future release.
 *
 * @deprecated
 */
@Deprecated
public class Util {

  /**
   *
   */
  private Util() {

  }

  /**
   * Determine if a string is truly a {@link UUID} string.
   *
   * Unfortunately, an exception will be thrown if it is not valid.
   *
   * @param uuidStr - {@link String} to check
   * @return true if a {@link UUID} string, otherwise false.
   */
  public static boolean isValidUUIDString(String uuidStr) {
    // Do what we can to avoid an exception.
    if (uuidStr != null && uuidStr.length() == 36 && uuidStr.contains("-"))
      // Could do more above, but just eat the exception
      // if it's invalid
      try {
        UUID.fromString(uuidStr);
        return true;
      } catch (IllegalArgumentException iae) {
        return false;
      }
    else
      return false;
  }
}
