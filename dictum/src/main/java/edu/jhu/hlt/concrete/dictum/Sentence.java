/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Optional;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public abstract class Sentence implements UUIDable, TextSpannable {
  Sentence() { }

  public abstract Optional<Tokenization> getPowerTokenization();

  public static class Builder extends Sentence_Builder {
    public Builder() {
      // defaults: UUID
      super.setUUID(UUID.randomUUID());
    }
  }
}
