package edu.jhu.hlt.concrete.validation.ff.structure;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.validation.ff.InvalidConcreteStructException;

public class TokenTaggings {

  private TokenTaggings() {
  }

  public static final ValidTokenTagging validate(final TokenTagging tt) throws InvalidConcreteStructException {
    return new NecessarilyUniqueUUIDTokenTagging(tt);
  }

  public static final List<ValidTokenTagging> extract(final Tokenization tkz) throws InvalidConcreteStructException {
    final int ps = tkz.getTokenTaggingListSize();
    List<ValidTokenTagging> pl = new ArrayList<>(ps);
    if (ps > 0)
      for (TokenTagging p : tkz.getTokenTaggingList())
        pl.add(validate(p));

    return pl;
  }
}
