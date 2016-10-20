/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public abstract class ArgumentProperty implements FlatMetadata {

  ArgumentProperty() {
  }

  public abstract String getValue();

  public abstract Optional<Double> getPolarity();

  public static class Builder extends ArgumentProperty_Builder {
    public Builder() {

    }
  }
}
