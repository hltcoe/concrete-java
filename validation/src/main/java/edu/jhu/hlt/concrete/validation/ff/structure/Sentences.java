package edu.jhu.hlt.concrete.validation.ff.structure;

import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class Sentences {
  private Sentences() {

  }

  public static final ValidSentence validate(Sentence st) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDSentence(st);
  }
}
