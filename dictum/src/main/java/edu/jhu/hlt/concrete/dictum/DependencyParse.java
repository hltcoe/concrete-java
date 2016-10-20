/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.List;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;

@FreeBuilder
public abstract class DependencyParse implements FlatMetadataWithUUID {
  public abstract List<Dependency> getDependencies();

  public static class Builder extends DependencyParse_Builder {
    public Builder() {
      // defaults: UUID, kbest = 1, ts = current system time.
      super.setUUID(UUID.randomUUID());
      super.setKBest(IntGreaterThanZero.create(1));
      super.setTimestamp(UnixTimestamp.now());
    }
  }

  DependencyParse() {
  }
}
