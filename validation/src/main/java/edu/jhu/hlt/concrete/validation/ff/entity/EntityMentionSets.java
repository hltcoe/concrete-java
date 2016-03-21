package edu.jhu.hlt.concrete.validation.ff.entity;

import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class EntityMentionSets {
  private EntityMentionSets() {

  }

  public static final ValidEntityMentionSet validate(EntityMentionSet ems) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDEntityMentionSet(ems);
  }
}
