/**
 * 
 */
package edu.jhu.hlt.concrete.examples;

/**
 * @author max
 *
 */
public class AnnotationException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -6601209923292147662L;

  /**
   * 
   */
  public AnnotationException() {
    this("There was an exception during annotation.");
  }

  /**
   * @param message
   */
  public AnnotationException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param cause
   */
  public AnnotationException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   */
  public AnnotationException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public AnnotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    // TODO Auto-generated constructor stub
  }

}
