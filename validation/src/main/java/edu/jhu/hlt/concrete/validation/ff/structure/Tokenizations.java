package edu.jhu.hlt.concrete.validation.ff.structure;

import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class Tokenizations {
  private Tokenizations() {

  }

  public static final ValidTokenization validate(final Tokenization p) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDTokenization(p);
  }
}
