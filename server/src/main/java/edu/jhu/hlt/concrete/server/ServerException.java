/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.server;

/**
 * Basic server exception wrapper.
 */
public class ServerException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * @param message
   */
  public ServerException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public ServerException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public ServerException(String message, Throwable cause) {
    super(message, cause);
  }
}
