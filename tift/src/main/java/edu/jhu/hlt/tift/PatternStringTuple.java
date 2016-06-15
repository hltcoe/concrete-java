/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift;

import java.util.regex.Pattern;

/**
 * Wrapper for mass <code>SimpleImmutableEntry[Pattern, String]</code> in TwitterTokenizer.
 */
public class PatternStringTuple {
  private final Pattern pattern;
  private final String entry;

  /**
   * Default constructor.
   *
   * @param pattern the pattern
   * @param entry the entry
   */
  public PatternStringTuple(Pattern pattern, String entry) {
    this.pattern = pattern;
    this.entry = entry;
  }

  /**
   * Compile the first parameter into a {@link java.util.regex.Pattern} object.
   *
   * @param pattern
   * @param entry
   */
  public PatternStringTuple(String pattern, String entry) {
    this.pattern = Pattern.compile(pattern);
    this.entry = entry;
  }

  /**
   * @return the pattern
   */
  public Pattern getPattern() {
    return pattern;
  }

  /**
   * @return the entry
   */
  public String getEntry() {
    return entry;
  }
}
