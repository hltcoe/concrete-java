/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Optional;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import com.google.common.collect.ImmutableSet;


/**
 * Interface supporting entities within a particular document.
 */
@FreeBuilder
public abstract class InDocEntity implements UUIDable, ConfidenceScorable {
  InDocEntity() {
  }

  public abstract ImmutableSet<UUID> getMentionUUIDs();

  public abstract Optional<String> getType();

  public abstract Optional<String> getCanonicalName();

  public static class Builder extends InDocEntity_Builder { }
}
