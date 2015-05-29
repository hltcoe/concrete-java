/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.base;

import edu.jhu.hlt.concrete.miscommunication.WrappedCommunication;
import edu.jhu.hlt.concrete.miscommunication.sentenced.NoEmptySentenceListOrTokenizedCommunication;

/**
 * Interface that produces {@link WrappedCommunication} implementations by requiring
 * a {@link NoEmptySentenceListOrTokenizedCommunication} as input.
 */
public interface NoEmptySentenceListOrTokenizedCommunicationAnalytic<T extends WrappedCommunication> extends Analytic<T> {
  /**
   * @param c a {@link NoEmptySentenceListOrTokenizedCommunication}
   * @return the annotated {@link WrappedCommunication} impl.
   * @throws AnalyticException on analytic error
   */
  public T annotate(NoEmptySentenceListOrTokenizedCommunication c) throws AnalyticException;
}
