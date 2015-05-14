/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication;

/**
 * Checked exception for use in Miscommunication library.
 */
public class MiscommunicationException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message info
   */
  public MiscommunicationException(String message) {
    super(message);
  }

  /**
   * @param cause why
   */
  public MiscommunicationException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message info
   * @param cause why
   */
  public MiscommunicationException(String message, Throwable cause) {
    super(message, cause);
  }
}
