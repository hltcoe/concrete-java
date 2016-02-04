/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.spans;

import java.util.function.Predicate;

import edu.jhu.hlt.concrete.TextSpan;

/**
 * Wrapper around Concrete {@link TextSpan} objects.
 */
public class TextSpanWrapper {

  public static Predicate<TextSpan> hasNonWhitespaceText(final String txt) {
    return ts -> {
      final int s = ts.getStart();
      final int e = ts.getEnding();
      final String ss = txt.substring(s, e);
      return !ss.trim().isEmpty();
    };
  }

  /**
   * @return a {@link Predicate} that tests if the {@link TextSpan}
   * has the same start and end, useful for detecting zero-length
   * text spans.
   */
  public static Predicate<TextSpan> hasZeroLength() {
    return ts -> ts.getStart() == ts.getEnding();
  }
}
