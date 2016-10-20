/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;

/**
 * Class that represents a group of {@link InDocEntityMention}
 * objects. Extends {@link FlatMetadataWithUUID}.
 */
@FreeBuilder
public abstract class InDocEntityMentionGroup implements FlatMetadataWithUUID {
  InDocEntityMentionGroup() {
  }

  public abstract Map<UUID, InDocEntityMention> getIdToEntityMentionMap();

  public static class Builder extends InDocEntityMentionGroup_Builder {
    public Builder() {
      // defaults: UUID, kbest = 1, ts = current system time.
      super.setUUID(UUID.randomUUID());
      super.setKBest(IntGreaterThanZero.create(1));
      super.setTimestamp(UnixTimestamp.now());
    }

    @Override
    public InDocEntityMentionGroup build() {
      InDocEntityMentionGroup g = super.build();
      // validate that all child pointers
      // are actually UUIDs in this set.
      Set<UUID> allMentionIDs = g.getIdToEntityMentionMap().keySet();
      g.getIdToEntityMentionMap().values()
          .forEach(ids -> {
            if (!allMentionIDs.containsAll(ids.getChildMentionUUIDs())) {
              throw new IllegalArgumentException("Child ID pointers for EntityMention "
                  + ids.getUUID().toString() + " include at least one UUID that is not a part of "
                  + "the UUIDs of this EntityMentionGroup.");
            }
          });
      return g;
    }
  }
}
