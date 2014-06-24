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
public class InvalidUUIDException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message
   */
  public InvalidUUIDException(String message) {
    super("Not a valid UUID: " + message);
  }
}
