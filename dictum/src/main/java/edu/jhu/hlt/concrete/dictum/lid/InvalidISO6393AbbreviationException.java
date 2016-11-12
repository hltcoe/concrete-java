/**
 *
 */
package edu.jhu.hlt.concrete.dictum.lid;

/**
 *
 */
public class InvalidISO6393AbbreviationException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * @param the invalid code
   */
  InvalidISO6393AbbreviationException(String code) {
    super(code + " is not a valid ISO-639-3 language code.");
  }
}
