/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.spans;

import edu.jhu.hlt.concrete.TextSpan;

/**
 * Utilities for working with Concrete {@link TextSpan} objects.
 */
public class TextSpanFactory {

  /**
   * 
   */
  private TextSpanFactory() {

  }

  /**
   * Create a {@link TextSpan} object with an offset applied to both the beginning
   * and end position.
   * 
   * @param begin inclusive start of the textspan
   * @param end exclusive end of the textspan
   * @param offset positive int to displace both begin and end
   * @return a {@link TextSpan} with an offset begin and ending
   * @throws IllegalArgumentException if the offset is negative
   */
  public static TextSpan withOffset(final int begin, final int end, final int offset) {
    if (offset < 0)
      throw new IllegalArgumentException("Can't have a negative offset.");
    final int oBegin = begin + offset;
    final int oEnd = end + offset;
    return new TextSpan()
        .setStart(oBegin)
        .setEnding(oEnd);
  }
}
