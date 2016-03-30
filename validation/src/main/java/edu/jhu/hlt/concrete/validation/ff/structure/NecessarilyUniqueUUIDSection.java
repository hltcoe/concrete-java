package edu.jhu.hlt.concrete.validation.ff.structure;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.validation.ff.AbstractConcreteStructWithNecessarilyUniqueUUIDs;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class NecessarilyUniqueUUIDSection extends AbstractConcreteStructWithNecessarilyUniqueUUIDs<Section>
    implements ValidSection {

  private List<ValidSentence> stl;

  NecessarilyUniqueUUIDSection(Section s) throws InvalidConcreteStructException {
    super(s);
    this.addNecessarilyUniqueUUID(s.getUuid());

    final int nSents = s.getSentenceListSize();
    this.stl = new ArrayList<>(nSents);
    if (nSents > 0)
      for (Sentence st : s.getSentenceList())
        this.stl.add(Sentences.validate(st));
  }
}
