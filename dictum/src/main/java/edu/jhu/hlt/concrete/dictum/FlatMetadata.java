/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.NonEmptyNonWhitespaceString;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;

/**
 * Interface capturing metadata about a particular annotation.
 */
public interface FlatMetadata {
  public NonEmptyNonWhitespaceString getTool();

  public IntGreaterThanZero getKBest();

  public UnixTimestamp getTimestamp();
}
