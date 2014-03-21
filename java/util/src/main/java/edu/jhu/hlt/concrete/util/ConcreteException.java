/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

/**
 * @author max
 *
 */
public class ConcreteException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -2031653431583973049L;

  /**
   * @param message
   */
  public ConcreteException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public ConcreteException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public ConcreteException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public ConcreteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
