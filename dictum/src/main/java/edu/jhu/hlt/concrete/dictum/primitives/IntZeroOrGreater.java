/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum.primitives;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.rules.Rules;

/**
 * Class representing an <code>int</code> that is greater than or equal to <code>0</code>.
 */
@FreeBuilder
public abstract class IntZeroOrGreater {
  IntZeroOrGreater() {
  }

  public abstract int getVal();

  /**
   * @param i the <code>int</code> to wrap
   * @return an {@link IntZeroOrGreater}
   */
  public static IntZeroOrGreater create (int i) {
    return new Builder().setVal(i).build();
  }

  public static class Builder extends IntZeroOrGreater_Builder {
    public Builder() {
    }

    @Override
    public Builder setVal(int i) {
      if (Rules.isGTOrEqualToZero().negate().test(i))
        throw new IllegalArgumentException("int must be >= 0");
      return super.setVal(i);
    }
  }
}
