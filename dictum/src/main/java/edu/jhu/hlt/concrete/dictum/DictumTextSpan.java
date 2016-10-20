/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Interface representing a {@link FlatTextSpan} capable of
 * retrieving the underlying text represented by the same in the document.
 * <br><br>
 * Useful for structures that define a text span that is optional.
 * With this interface, one can obtain the underlying string captured by the
 * underlying {@link FlatTextSpan} object.
 *
 * @see FlatTextSpan
 */
@FreeBuilder
public abstract class DictumTextSpan extends FlatTextSpan {
  /**
   * @return the text encompassed by this {@link FlatTextSpan}
   */
  public abstract String getTextFromDocument();

  public static class Builder extends DictumTextSpan_Builder {
    public Builder() {
    }
  }

  DictumTextSpan() {
  }
}
