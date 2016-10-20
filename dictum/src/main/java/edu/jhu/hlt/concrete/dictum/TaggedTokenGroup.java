/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;

/**
 * Abstract class that represents a group of {@link TaggedToken}
 * objects. Extends {@link FlatMetadataWithUUID}.
 */
@FreeBuilder
public abstract class TaggedTokenGroup implements FlatMetadataWithUUID {
  TaggedTokenGroup() {
  }

  public abstract Optional<String> getTaggingType();

  public abstract Map<Integer, TaggedToken> getIndexToTaggedTokenMap();

  public static class Builder extends TaggedTokenGroup_Builder {
    public Builder() {
      // defaults: UUID, kbest = 1, ts = current system time.
      super.setUUID(UUID.randomUUID());
      super.setKBest(IntGreaterThanZero.create(1));
      super.setTimestamp(UnixTimestamp.now());
    }
  }
}
