/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation.ff.entity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.UUID;
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
public class FailFastEntity extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<Entity>
    implements ValidEntity {

  private final ValidUUID uuid;
  private final Set<ValidUUID> menIDs;
  private final Optional<String> type;
  private final Optional<String> canonicalName;
  private final Optional<Double> conf;

  /**
   * @throws InvalidConcreteStructException on invalid {@link Entity} (duplicate UUIDs)
   */
  FailFastEntity(final Entity e) throws InvalidConcreteStructException {
    super(e);
    this.uuid = UUIDs.validate(e.getUuid());
    this.addNecessarilyUniqueUUID(this.uuid);
    final int mls = e.getMentionIdListSize();
    this.menIDs = new HashSet<>(mls);

    if (mls > 0)
      for (UUID id : e.getMentionIdList())
        if (!this.menIDs.add(UUIDs.validate(id)))
          throw new InvalidConcreteStructException("At least one mention ID is duplicated: " + id.toString());

    this.type = Optional.ofNullable(e.getType());
    this.canonicalName = Optional.ofNullable(e.getCanonicalName());
    this.conf = Optional.ofNullable(e.getConfidence());

    if (this.conf.isPresent()) {
      Double d = this.conf.get();
      if (d.isNaN()
          || d.isInfinite())
        throw new InvalidConcreteStructException("NaN, +inf, and -inf values are not supported for confidences.");
    }
  }

  @Override
  public Set<ValidUUID> getMentionIDSet() {
    return new HashSet<>(menIDs);
  }

  @Override
  public Optional<String> getType() {
    return this.type;
  }

  @Override
  public Optional<String> getCanonicalName() {
    return this.canonicalName;
  }

  @Override
  public Optional<Double> getConfidence() {
    return this.conf;
  }

  @Override
  public ValidUUID getUUID() {
    return this.uuid;
  }
}
