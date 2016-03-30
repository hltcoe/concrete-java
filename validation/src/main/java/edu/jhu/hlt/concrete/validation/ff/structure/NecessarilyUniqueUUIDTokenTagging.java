package edu.jhu.hlt.concrete.validation.ff.structure;

import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class NecessarilyUniqueUUIDTokenTagging extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<TokenTagging>
    implements ValidTokenTagging {

  NecessarilyUniqueUUIDTokenTagging(TokenTagging tkz) throws InvalidConcreteStructException {
    super(tkz);
    this.addNecessarilyUniqueUUID(tkz.getUuid());
  }
}
