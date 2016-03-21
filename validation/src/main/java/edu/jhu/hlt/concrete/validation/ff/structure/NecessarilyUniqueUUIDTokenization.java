package edu.jhu.hlt.concrete.validation.ff.structure;

import java.util.List;

import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class NecessarilyUniqueUUIDTokenization extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<Tokenization>
    implements ValidTokenization {

  private List<ValidParse> parses;
  private List<ValidDependencyParse> dps;
  private List<ValidTokenTagging> tts;

  NecessarilyUniqueUUIDTokenization(Tokenization tkz) throws InvalidConcreteStructException {
    super(tkz);
    this.addNecessarilyUniqueUUID(tkz.getUuid());

    this.parses = Parses.extract(tkz);
    this.dps = DependencyParses.extract(tkz);
    this.tts = TokenTaggings.extract(tkz);
  }
}
