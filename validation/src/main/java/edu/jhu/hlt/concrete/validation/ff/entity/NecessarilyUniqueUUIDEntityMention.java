/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation.ff.entity;

import java.util.List;
import java.util.Optional;

import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;
import edu.jhu.hlt.concrete.validation.ff.UUIDs;
import edu.jhu.hlt.concrete.validation.ff.ValidUUID;

/**
 * Implementation of {@link ValidEntity} that makes the following assumptions:
 * <ul>
 * <li>if set, the list of mention UUIDs are unique</li>
 * </ul>
 */
public class NecessarilyUniqueUUIDEntityMention extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<EntityMention>
    implements ValidEntityMention {

  private final ValidUUID uuid;

  private final Optional<String> entityType;
  private final Optional<String> phraseType;
  private final Optional<String> text;
  private final Optional<Double> conf;

  /**
   * @throws InvalidConcreteStructException on invalid {@link Entity} (duplicate UUIDs)
   */
  NecessarilyUniqueUUIDEntityMention(final EntityMention e) throws InvalidConcreteStructException {
    super(e);
    this.uuid = UUIDs.validate(e.getUuid());
    this.addNecessarilyUniqueUUID(this.uuid);

    this.entityType = Optional.ofNullable(e.getEntityType());
    this.phraseType = Optional.ofNullable(e.getPhraseType());
    this.text = Optional.ofNullable(e.getText());
    this.conf = Optional.ofNullable(e.getConfidence());
  }

  public Optional<String> getEntityType() {
    return entityType;
  }

  public Optional<String> getPhraseType() {
    return phraseType;
  }

  public Optional<String> getText() {
    return text;
  }

  public Optional<Double> getConf() {
    return conf;
  }

  @Override
  public ValidUUID getUUID() {
    return this.uuid;
  }

  @Override
  public List<Token> getTokens() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<Token> getAnchorToken() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ValidUUID getTokenizationUUID() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Optional<TextSpan> getTextSpan() {
    // TODO Auto-generated method stub
    return null;
  }
}
