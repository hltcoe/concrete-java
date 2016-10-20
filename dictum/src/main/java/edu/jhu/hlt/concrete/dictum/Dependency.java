/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.rules.Rules;

/**
 * Class that represents a Dependency, part of a {@link DependencyParse}.
 */
@FreeBuilder
public abstract class Dependency {

  /**
   * @return the index of the governor, or head token.
   */
  public abstract Optional<Integer> getGovernorIndex();

  /**
   * @return the index of the dependent token.
   */
  public abstract int getDependentIndex();

  public abstract Optional<String> getEdgeType();

  public static class Builder extends Dependency_Builder {
    public Builder() {

    }

    @Override
    public Builder setDependentIndex(int d) {
      if (Rules.isGTOrEqualToZero().test(d))
        return super.setDependentIndex(d);
      else
        throw new IllegalArgumentException("Dep cannot be <0.");
    }
  }

  Dependency() { }

}
