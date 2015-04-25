/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.requirements;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Tokenization;

/**
 * Interface that serves as the base interface for analytic requirements.
 * <br>
 * <br>
 * Many analytics have certain requirements (e.g., that {@link Section}s exist on
 * a {@link Communication}) before they are capable of running. 
 */
public interface AnalyticRequirement {
  /**
   * Determine if this analytic is capable of annotating the given {@link Communication}.
   * <br>
   * <br>
   * For example, some analytics may require {@link Tokenization} objects; if so,
   * their implementation of this method should check for those.
   * 
   * @param comm
   *          the {@link Communication} to check
   * @return true iff this analytic is capable of annotating the given communication
   */
  public boolean isSatisfactory(Communication comm);
}
