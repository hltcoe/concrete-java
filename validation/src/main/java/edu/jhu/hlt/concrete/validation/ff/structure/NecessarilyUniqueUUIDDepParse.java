package edu.jhu.hlt.concrete.validation.ff.structure;

import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class NecessarilyUniqueUUIDDepParse extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<DependencyParse>
    implements ValidDependencyParse{

  public NecessarilyUniqueUUIDDepParse(DependencyParse dp) throws InvalidConcreteStructException {
    super(dp);
    this.addNecessarilyUniqueUUID(dp.getUuid());
  }
}
