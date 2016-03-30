package edu.jhu.hlt.concrete.validation.ff.structure;

import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class NecessarilyUniqueUUIDParse extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<Parse>
    implements ValidParse {

  public NecessarilyUniqueUUIDParse(Parse p) throws InvalidConcreteStructException {
    super(p);
    this.addNecessarilyUniqueUUID(p.getUuid());
  }
}
