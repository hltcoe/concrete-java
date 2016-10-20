/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Class that represents a constituent, part of a {@link Parse}.
 */
@FreeBuilder
public abstract class Constituent {
  Constituent() {
  }

  public abstract int getId();

  public abstract Optional<String> getTag();

  public abstract List<Integer> getChildList();

  public abstract Optional<Integer> getHeadChildIndex();

  public abstract Optional<Integer> getStart();

  public abstract Optional<Integer> getEnd();

  public static class Builder extends Constituent_Builder {
    public Builder () {

    }
  }
}
