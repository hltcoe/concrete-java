/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;


/**
 * Class, extending {@link FlatTokenGrouping}, that represents
 * a way to point to specific tokens inside a structure.
 * This interface provides an additional set of methods that obviate the
 * need for construction of pointers, crawling through tokenizations, etc.
 * <br><br>
 * One can consume this interface's methods to get wrapper objects that
 * allow direct retrieval of the tokens, their textspans,
 * and their texts.
 */
@FreeBuilder
public abstract class DictumTokenGrouping extends FlatTokenGrouping {
  public abstract List<Token> getTokens();

  public abstract Optional<Token> getAnchorToken();

  public abstract Tokenization getTokenization();

  public static class Builder extends DictumTokenGrouping_Builder { }

  DictumTokenGrouping() { }
}
