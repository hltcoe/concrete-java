/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.base;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.miscommunication.WrappedCommunication;
import edu.jhu.hlt.concrete.miscommunication.sectioned.NonSentencedSectionedCommunication;

/**
 * Interface representing analytics that are run on {@link Communication} objects
 * that have {@link Section}s, but that do not have {@link Sentence}s.
 */
public interface NonSentencedSectionedCommunicationAnalytic<T extends WrappedCommunication> extends Analytic<T> {
  /**
   * @param c a {@link NonSentencedSectionedCommunication} to annotate
   * @return a {@link WrappedCommunication} implementation with the analytic's annotations
   * @throws AnalyticException on analytic error
   */
  public T annotate(NonSentencedSectionedCommunication c) throws AnalyticException;
}
