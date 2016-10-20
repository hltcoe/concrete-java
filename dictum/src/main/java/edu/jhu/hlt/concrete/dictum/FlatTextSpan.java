/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.rules.Rules;

/**
 * A flat version of a text span, with a beginning and end.
 * Additionally, the following validation checks are performed:
 * <ul>
 * <li>the start is non-negative</li>
 * <li>the end is greater than the start</li>
 * </ul>
 */
@FreeBuilder
public abstract class FlatTextSpan {
  public abstract int getStart();

  public abstract int getEnd();

  public static class Builder extends FlatTextSpan_Builder {
    public Builder() {

    }

    @Override
    public Builder setStart(int s) {
      if (Rules.isGTOrEqualToZero().test(s))
        return super.setStart(s);
      else
        throw new IllegalArgumentException("Start failed validation: was not >= 0.");
    }

    @Override
    public FlatTextSpan build() {
      FlatTextSpan fts = super.build();
      if (Rules.isGT(fts.getStart())
          .test(fts.getEnd()))
        return fts;
      else
        throw new IllegalArgumentException("End failed validation.");
    }
  }

  FlatTextSpan () { }
}
