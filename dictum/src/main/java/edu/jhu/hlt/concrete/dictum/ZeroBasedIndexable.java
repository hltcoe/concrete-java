/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import edu.jhu.hlt.concrete.dictum.primitives.IntZeroOrGreater;

/**
 * Interface representing an object that has a zero-based index field.
 */
public interface ZeroBasedIndexable {
  public IntZeroOrGreater getIndex();
}
