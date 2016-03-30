package edu.jhu.hlt.concrete.validation.ff.structure;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class DependencyParses {

  private DependencyParses() {
  }

  public static final ValidDependencyParse validate(final DependencyParse dp) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDDepParse(dp);
  }

  public static final List<ValidDependencyParse> extract(final Tokenization tkz) throws InvalidConcreteStructException {
    final int ps = tkz.getDependencyParseListSize();
    List<ValidDependencyParse> pl = new ArrayList<>(ps);
    if (ps > 0)
      for (DependencyParse p : tkz.getDependencyParseList())
        pl.add(validate(p));

    return pl;
  }
}
