package edu.jhu.hlt.concrete.validation.ff.structure;

import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class NecessarilyUniqueUUIDSentence extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<Sentence>
    implements ValidSentence {

  private final ValidTokenization tkz;

  public NecessarilyUniqueUUIDSentence(Sentence s) throws InvalidConcreteStructException {
    super(s);
    this.addNecessarilyUniqueUUID(s.getUuid());
    this.tkz = Tokenizations.validate(s.getTokenization());
  }
}
