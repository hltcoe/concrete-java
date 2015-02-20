/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.safe;

/**
 * Checked exception that occurs when an unsafe Concrete object
 * is created by a "safety" method.
 */
public class SafetyException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * @param message
   */
  public SafetyException(final String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public SafetyException(final Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public SafetyException(String message, Throwable cause) {
    super(message, cause);
  }
}
