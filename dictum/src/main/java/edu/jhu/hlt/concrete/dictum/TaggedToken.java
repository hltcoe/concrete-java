/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public abstract class TaggedToken extends Token implements ConfidenceScorable {
  public abstract String getTag();

  public static class Builder extends TaggedToken_Builder {
    public Builder() {

    }
  }

  TaggedToken() { }
}
