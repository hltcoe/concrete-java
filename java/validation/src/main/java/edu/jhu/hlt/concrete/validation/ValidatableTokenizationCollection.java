/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.TokenizationCollection;

/**
 * @author max
 *
 */
public class ValidatableTokenizationCollection extends AbstractAnnotation<TokenizationCollection> {


  public ValidatableTokenizationCollection(TokenizationCollection annotation) {
    super(annotation);
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.rebar.ballast.validation.AbstractAnnotation#isValid(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public boolean isValidWithComm(Communication c) {
    return true;
  }

  @Override
  public boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }
}
