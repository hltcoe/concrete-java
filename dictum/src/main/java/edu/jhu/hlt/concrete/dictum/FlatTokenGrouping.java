/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.rules.Rules;
/**
 * Flattened interface, created for representing pointers to groups of tokens.
 * Extends {@link TextSpannable}. The following validation checks are performed:
 * <ul>
 * <li><code>tokenIndices</code>: must be free of duplicates and contain no
 * negative elements</li>
 * <li><code>anchorTokenIndex</code>: if present, must be in the token indices
 * field</li>
 * </ul>
 */
@FreeBuilder
public abstract class FlatTokenGrouping implements TextSpannable {
  public abstract List<Integer> getTokenIndices();

  public abstract Optional<Integer> getAnchorTokenIndex();

  public abstract UUID getTokenizationUUID();

  public static class Builder extends FlatTokenGrouping_Builder {
    public Builder() { }

    @Override
    public Builder addTokenIndices(int next) {
      if (Rules.isGTOrEqualToZero().test(next))
        return super.addTokenIndices(next);
      else
        throw new IllegalArgumentException("Token index addition failed.");
    }

    @Override
    public FlatTokenGrouping build() {
      FlatTokenGrouping trs = super.build();
      List<Integer> il = trs.getTokenIndices();
      Set<Integer> is = new HashSet<>(il);
      trs.getAnchorTokenIndex().ifPresent(aidx -> {
        if (!is.contains(aidx))
          throw new IllegalArgumentException("Anchor token index set but is not in list of tokens.");
      });
      if (il.size() == is.size())
        return trs;
      else
        throw new IllegalArgumentException("Validation failed: token list contains duplicates.");
    }
  }

  FlatTokenGrouping() { }
}
