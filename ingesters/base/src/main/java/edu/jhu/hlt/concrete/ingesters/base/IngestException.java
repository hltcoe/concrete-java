/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.ingesters.base;

/**
 * Checked {@link Exception} for use in Concrete {@link Ingester} machinations.
 */
public class IngestException extends Exception {

  private static final long serialVersionUID = 5322456791047723107L;

  /**
   * @param message
   */
  public IngestException(String message) {
    super(message);
  }

  /**
   * @param message
   * @param cause
   */
  public IngestException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param cause
   */
  public IngestException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public IngestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
