/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public abstract class Argument {

  Argument() {
  }

  public abstract Optional<String> getRole();

  public abstract Optional<UUID> getEntityId();

  public abstract Optional<UUID> getSituationId();

  public abstract List<ArgumentProperty> getProperties();

  public static class Builder extends Argument_Builder {
    public Builder() {

    }
  }
}
