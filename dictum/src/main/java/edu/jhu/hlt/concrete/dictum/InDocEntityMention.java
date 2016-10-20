/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;


/**
 * Class that represents a mention of an entity within a particular document.
 * Extends {@link DictumTokenGrouping}.
 *
 * @see UUIDable
 * @see ConfidenceScorable
 */
@FreeBuilder
public abstract class InDocEntityMention extends DictumTokenGrouping
    implements UUIDable, ConfidenceScorable {
  public abstract Optional<String> getText();

  public abstract Optional<String> getEntityType();

  public abstract Optional<String> getPhraseType();

  public abstract Set<UUID> getChildMentionUUIDs();

  public static class Builder extends InDocEntityMention_Builder {

  }

  InDocEntityMention() {
  }
}
