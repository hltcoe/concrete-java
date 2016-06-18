/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift;

import java.util.regex.Pattern;

/**
 * Wrapper for mass <code>SimpleImmutableEntry[Pattern, String]</code> in TwitterTokenizer.
 */
class PatternStringTuple {
  private final Pattern pattern;
  private final String entry;

  /**
   * Default constructor.
   *
   * @param pattern the pattern
   * @param entry the entry
   */
  PatternStringTuple(Pattern pattern, String entry) {
    this.pattern = pattern;
    this.entry = entry;
  }

  /**
   * Compile the first parameter into a {@link java.util.regex.Pattern} object.
   *
   * @param pattern
   * @param entry
   */
  PatternStringTuple(String pattern, String entry) {
    this.pattern = Pattern.compile(pattern);
    this.entry = entry;
  }

  /**
   * @return the pattern
   */
  Pattern getPattern() {
    return pattern;
  }

  /**
   * @return the entry
   */
  String getEntry() {
    return entry;
  }
}
