/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public abstract class Section implements UUIDable, TextSpannable {
  Section() {
  }

  public abstract Map<UUID, Sentence> getIdToSentenceMap();

  public abstract String getKind();

  public abstract Optional<String> getLabel();

  public abstract List<Integer> getNumbers();

  public static class Builder extends Section_Builder {
    public Builder() {
      // defaults: UUID
      super.setUUID(UUID.randomUUID());
    }
  }
}
