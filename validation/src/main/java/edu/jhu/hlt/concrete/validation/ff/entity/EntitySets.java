package edu.jhu.hlt.concrete.validation.ff.entity;

import edu.jhu.hlt.concrete.EntitySet;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class EntitySets {

  private EntitySets() {

  }

  public static final ValidEntitySet validate(EntitySet es) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDEntitySet(es);
  }
}
