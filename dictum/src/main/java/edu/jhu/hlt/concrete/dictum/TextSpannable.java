/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Optional;

/**
 * Interface that represents an object with an optional
 * {@link FlatTextSpan}.
 */
public interface TextSpannable {
  /**
   * @return an {@link Optional} wrapping a {@link FlatTextSpan}
   */
  public Optional<FlatTextSpan> getTextSpan();
}
