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

@FreeBuilder
public abstract class InDocEntityGroup implements FlatMetadataWithUUID {
  InDocEntityGroup() {
  }

  public abstract Map<UUID, InDocEntity> getIdToEntityMap();

  public abstract Optional<UUID> getMentionSetUUID();

  public static class Builder extends InDocEntityGroup_Builder {
    /**
     * Return a {@link Builder} with a kbest of <code>1</code>
     * and a timestamp of the current system unix time.
     */
    public Builder() {
      // defaults: UUID, kbest = 1, ts = current system time.
      super.setUUID(UUID.randomUUID());
      super.setKBest(IntGreaterThanZero.create(1));
      super.setTimestamp(UnixTimestamp.now());
    }
  }
}
