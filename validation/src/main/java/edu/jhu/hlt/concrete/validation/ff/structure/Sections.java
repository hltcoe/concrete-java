package edu.jhu.hlt.concrete.validation.ff.structure;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class Sections {

  private Sections() {

  }

  public static final ValidSection validate(Section s) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDSection(s);
  }
}
