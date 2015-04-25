/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.base;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;

/**
 * Interface that extends {@link Analytic} by adding a method to check whether a 
 * {@link Communication} can be annotated by this analytic. 
 * <br>
 * <br>
 * Useful for analytics that require certain annotations (e.g., {@link Section}s) 
 * before they can perform their functions.
 */
public interface DependentAnalytic extends Analytic {
  
  public boolean isAnnotatable(Communication comm);
}
