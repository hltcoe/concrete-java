/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public abstract class MentionArgument implements ConfidenceScorable, TokenGroupable {

  public abstract Optional<String> getRole();

  public abstract Optional<UUID> getEntityMentionId();

  public abstract Optional<UUID> getSituationMentionId();

  public abstract List<ArgumentProperty> getProperties();

  MentionArgument() {
  }

  public static class Builder extends MentionArgument_Builder {
    public Builder() {

    }
  }
}
