package edu.jhu.hlt.concrete.validation.ff.entity;

import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class Entities {
  private Entities() {

  }

  public static final ValidEntity validate(Entity e) throws InvalidConcreteStructException {
    return new FailFastEntity(e);
  }
}
