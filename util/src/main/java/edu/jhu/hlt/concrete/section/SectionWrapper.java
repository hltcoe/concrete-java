/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.section;

import java.util.function.Predicate;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.spans.TextSpanWrapper;

/**
 * Wrappers and utilities for working with concrete {@link Section}
 * objects.
 */
public class SectionWrapper {
  /**
   *
   * @return a {@link Predicate} that evaluates if the {@link Section} in
   * question has a {@link TextSpan} with zero length (e.g., <code>start == end</code>).
   */
  public static final Predicate<Section> hasZeroLengthTextSpan() {
    return s -> TextSpanWrapper.hasZeroLength().test(s.getTextSpan());
  }
}
