/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.base;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.miscommunication.WrappedCommunication;
import edu.jhu.hlt.concrete.miscommunication.sectioned.SectionedCommunication;

/**
 * Analytic that depends upon Concrete {@link Section} objects inside
 * the {@link Communication} (at least).
 */
public interface SectionedCommunicationAnalytic<T extends WrappedCommunication> extends Analytic<T> {
  /**
   * @param sc a {@link SectionedCommunication} object
   * @throws AnalyticException on analytic error
   */
  public T annotate(SectionedCommunication sc) throws AnalyticException;
}
