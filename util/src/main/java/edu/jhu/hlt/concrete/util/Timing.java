/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Class that provides utility methods for generating Unix timestamps
 * based on time zones.
 */
public class Timing {

  private Timing() {

  }

  /**
   *
   * @return the current Unix time, based on UTC.
   */
  public static final long currentUTCTime() {
    return new DateTime(DateTimeZone.UTC).getMillis() / 1000;
  }

  /**
   * @return the current Unix local time.
   */
  public static final long currentLocalTime() {
    return new DateTime().getMillis() / 1000;
  }
}
