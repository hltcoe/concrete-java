/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.base;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication;

/**
 * Analytic that depends upon Concrete {@link Tokenization} objects inside
 * the {@link Communication} (at least).
 */
public interface TokenizationedCommunicationAnalytic extends Analytic {
  /**
   * @param sc a {@link TokenizedCommunication} object
   * @return a {@link Communication} with the analytic's annotations
   * @throws AnalyticException on analytic error
   */
  public Communication annotate(TokenizedCommunication sc) throws AnalyticException;
}
