package edu.jhu.hlt.concrete.validation.ff.entity;

import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class EntityMentions {

  private EntityMentions() {

  }

  public static final ValidEntityMention validate(final EntityMention em) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDEntityMention(em);
  }
}
