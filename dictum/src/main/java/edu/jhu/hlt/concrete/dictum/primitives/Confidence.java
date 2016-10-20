/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum.primitives;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.rules.Rules;

/**
 * Representation of a confidence score.
 */
@FreeBuilder
public abstract class Confidence {
  public abstract double getScore();

  /**
   * @param conf a <code>double</code>
   * @return a {@link Confidence} wrapping the parameter
   * @throws IllegalArgumentException if parameter is out of range [0.0, 1.0]
   */
  public static Confidence fromDouble(double conf) {
    return new Builder()
        .setScore(conf)
        .build();
  }

  public static class Builder extends Confidence_Builder {
    public Builder() {
    }

    @Override
    public Builder setScore(double score) {
      if (Rules.isBetweenZeroAndOneInclusive().test(score))
        return super.setScore(score);
      else
        throw new IllegalArgumentException("Invalid confidence score: " + score);
    }
  }

  Confidence () { }
}
