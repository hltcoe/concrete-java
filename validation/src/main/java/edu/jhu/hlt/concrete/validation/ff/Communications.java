package edu.jhu.hlt.concrete.validation.ff;

import edu.jhu.hlt.concrete.Communication;

public class Communications {
  private Communications() {

  }

  public static final ValidCommunication validate(Communication c) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDCommunication(c);
  }
}
