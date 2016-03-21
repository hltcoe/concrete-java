package edu.jhu.hlt.concrete.validation.ff.structure;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class Parses {
  private Parses() {

  }

  public static final ValidParse validate(final Parse p) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDParse(p);
  }

  public static final List<ValidParse> extract(final Tokenization tkz) throws InvalidConcreteStructException {
    final int ps = tkz.getParseListSize();
    List<ValidParse> pl = new ArrayList<>(ps);
    if (ps > 0)
      for (Parse p : tkz.getParseList())
        pl.add(validate(p));

    return pl;
  }
}
