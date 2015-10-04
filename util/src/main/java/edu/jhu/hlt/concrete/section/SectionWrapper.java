/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.section;

import java.util.function.Predicate;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.spans.TextSpanWrapper;

/**
 * Wrappers and utilities for working with concrete {@link Section}
 * objects.
 */
public class SectionWrapper {
  public static final Predicate<Section> hasZeroLengthTextSpan() {
    return s -> TextSpanWrapper.hasZeroLength().test(s.getTextSpan());
  }
}
