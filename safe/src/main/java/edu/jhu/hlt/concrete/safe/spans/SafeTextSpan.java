/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.safe.spans;

import edu.jhu.hlt.concrete.TextSpan;

/**
 * Interface with required Concrete {@link TextSpan} fields.
 */
public interface SafeTextSpan {
  /**
   * @return the start character offset as an int (inclusive)
   */
  public int getStart();
  
  /**
   * @return the end character offset as an int (exclusive)
   */
  public int getEnding();
}
