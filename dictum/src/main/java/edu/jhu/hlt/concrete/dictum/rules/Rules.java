/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum.rules;

import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

/**
 * Set of {@link Predicate} objects supporting validation of
 * dictum objects.
 */
public class Rules {

  /**
   *
   */
  private Rules() {
  }

  public static final Predicate<List<?>> containsNoDuplicates() {
    return i -> new HashSet<>(i).size() == i.size();
  }

  public static final Predicate<String> isEmptyOrWhitespaceOnly() {
    return s -> s.trim().isEmpty();
  }

  public static final Predicate<Double> isBetweenZeroAndOneInclusive() {
    return c -> c <= 1.0d && c >= 0.0d;
  }

  public static final Predicate<Integer> isGTZero() {
    return c -> c > 0;
  }

  public static final Predicate<Integer> isGTOrEqualToZero() {
    return c -> c >= 0;
  }

  public static final Predicate<Integer> isGT(int comparedTo) {
    return x -> x > comparedTo;
  }

  public static final Predicate<Long> isReasonableUnixTimestamp() {
    return k -> k < System.currentTimeMillis() / 100;
  }
}
