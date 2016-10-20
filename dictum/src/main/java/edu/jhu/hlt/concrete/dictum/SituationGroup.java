/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Map;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;

@FreeBuilder
public abstract class SituationGroup implements FlatMetadataWithUUID {
  public abstract Map<UUID, Situation> getIdToSituationMap();

  public static class Builder extends SituationGroup_Builder {
    public Builder() {
      // defaults: UUID, kbest = 1, ts = current system time.
      super.setUUID(UUID.randomUUID());
      super.setKBest(IntGreaterThanZero.create(1));
      super.setTimestamp(UnixTimestamp.now());
    }
  }

  SituationGroup() {
  }
}
