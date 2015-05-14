/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.base;

/**
 * Checked exception for use in Concrete analytics.
 */
public class AnalyticException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * @param message the message
   */
  public AnalyticException(String message) {
    super(message);
  }

  /**
   * @param cause why
   */
  public AnalyticException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message the message
   * @param cause why
   */
  public AnalyticException(String message, Throwable cause) {
    super(message, cause);
  }
}
