/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package concrete.ontology;

/**
 * @author max
 *
 */
public class InvalidConcreteTypeException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = -1068547726911898063L;

  /**
   * @param message
   */
  public InvalidConcreteTypeException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public InvalidConcreteTypeException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public InvalidConcreteTypeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public InvalidConcreteTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
