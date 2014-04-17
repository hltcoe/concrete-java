/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Token;

/**
 * @author max
 *
 */
public class ValidatableToken extends AbstractAnnotation<Token> {

  /**
   * @param annotation
   */
  public ValidatableToken(Token annotation) {
    super(annotation);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    // TODO Auto-generated method stub
    return false;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }
}
