/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.requirements;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;

/**
 * Implementation of {@link AnalyticRequirement} that enforces
 * the presence of {@link Section} objects.
 */
public class SectionedRequirement implements AnalyticRequirement {

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.analytics.requirements.AnalyticRequirement#isSatisfactory(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public boolean isSatisfactory(Communication comm) {
    return comm.isSetSectionList() && comm.getSectionListSize() > 0;
  }
}
