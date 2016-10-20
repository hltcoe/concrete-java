/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public abstract class SituationMention implements UUIDable, PolarityIntensityConfidenceScorable,
    TokenGroupable {
  SituationMention() { }

  public abstract String getSituationType();

  public abstract Optional<String> getSituationKind();

  public abstract List<Argument> getArguments();

  public abstract Set<UUID> getMentionIDs();

  public abstract List<Justification> getJustifications();

  public static class Builder extends SituationMention_Builder {

  }
}
