/*
 * 
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
