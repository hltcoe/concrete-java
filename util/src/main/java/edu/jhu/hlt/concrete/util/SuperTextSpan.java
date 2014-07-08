/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.TextSpan;

/**
 * A wrapper around {@link TextSpan} offering improved functionality. 
 * 
 * @author max
 */
public class SuperTextSpan {

  private final TextSpan ts;
  private final Communication c;
  
  /**
   * Create a {@link SuperTextSpan} object.
   * The {@link Communication} object should be the one that is associated
   * with the TextSpan that was passed in.
   *  
   * @param ts - a {@link TextSpan} object
   * @param c - a {@link Communication} object from which the {@link TextSpan} object was generated
   */
  public SuperTextSpan(TextSpan ts, Communication c) {
    this.ts = ts;
    this.c = c;
  }

  /**
   * Get the text from a {@link TextSpan}. May throw an {@link IndexOutOfBoundsException}
   * if the {@link Communication} and {@link TextSpan} do not align properly.
   * 
   * @return a {@link String} with the text of the {@link TextSpan} object. 
   */
  public String getText() {
    return c
        .getProcessedContent()
        .substring(ts.getStart(), ts.getEnding());
  }
}
