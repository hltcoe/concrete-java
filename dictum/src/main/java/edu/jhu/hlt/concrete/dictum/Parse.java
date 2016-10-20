/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;

/**
 * Class, extending {@link FlatMetadataWithUUID}, that represents a collection
 * of {@link Constituent} objects, as well as a UUID and metadata.
 * <br><br>
 * Each {@link Constituent#getChildList()} is also checked to ensure that
 * each child pointer is present in this annotation.
 */
@FreeBuilder
public abstract class Parse implements FlatMetadataWithUUID {

  private static final Logger LOGGER = LoggerFactory.getLogger(Parse.class);

  Parse() {
  }

  public abstract Map<Integer, Constituent> getConstituents();

  public static class Builder extends Parse_Builder {
    public Builder() {
      // defaults: UUID, kbest = 1, ts = current system time.
      super.setUUID(UUID.randomUUID());
      super.setKBest(IntGreaterThanZero.create(1));
      super.setTimestamp(UnixTimestamp.now());
    }

    @Override
    public Parse build() {
      Parse pp = super.build();
      Map<Integer, Constituent> m = pp.getConstituents();
      Set<Integer> idxs = m.keySet();
      List<Integer> il = new ArrayList<>(idxs);
      for (int i = 0; i < m.size(); i++) {
        Integer n = il.get(i);
        if (n.intValue() != i)
          throw new IllegalArgumentException("Index should be " + i
              + ", but instead it was " + n);
      }
      m.forEach((i, c) -> {
        LOGGER.debug("Validating constituent: {}", i);
        // ensure all children are present in this annotation
        if (!idxs.containsAll(c.getChildList()))
          throw new IllegalArgumentException("Not all constituent children indices "
              + "are present in this Constituent list.");
      });
      return pp;
    }
  }
}
