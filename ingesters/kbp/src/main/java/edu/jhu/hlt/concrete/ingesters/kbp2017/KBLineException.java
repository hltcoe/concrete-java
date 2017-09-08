package edu.jhu.hlt.concrete.ingesters.kbp2017;

public class KBLineException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public KBLineException(String line) {
    super("Don't know how to handle line: " + line);
  }
}
