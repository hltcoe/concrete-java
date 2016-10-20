/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum.conversion;

/**
 * Checked exception used when implementations of dictum-based
 * code fails validations.
 */
public class InvalidStructException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = -2301614993021859389L;

  public InvalidStructException(String message) {
    super(message);
  }

  public InvalidStructException(Throwable cause) {
    super(cause);
  }

  public InvalidStructException(String message, Throwable cause) {
    super(message, cause);
  }
}
