/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Map;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.dictum.lid.ValidISO3Languages;
import edu.jhu.hlt.concrete.dictum.primitives.Confidence;
import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;

/**
 *
 */
@FreeBuilder
public abstract class LanguageID implements FlatMetadataWithUUID {

  public abstract Map<String, Confidence> getLanguageToProbMap();

  LanguageID() {
  }

  public static class Builder extends LanguageID_Builder {
    public Builder() {
      // defaults: UUID, kbest = 1, ts = current system time.
      super.setUUID(UUID.randomUUID());
      super.setKBest(IntGreaterThanZero.create(1));
      super.setTimestamp(UnixTimestamp.now());
    }

    @Override
    public Builder putLanguageToProbMap(String abbr, Confidence val) {
      if (!ValidISO3Languages.isValidISO3Abbreviation(abbr))
        throw new IllegalArgumentException("Invalid ISO3 code: " + abbr);
      return super.putLanguageToProbMap(abbr, val);
    }
  }
}
