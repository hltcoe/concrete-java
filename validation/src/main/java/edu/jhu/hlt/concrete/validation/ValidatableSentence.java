/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Sentence;

/**
 * @author max
 * 
 */
public class ValidatableSentence extends AbstractAnnotation<Sentence> {

  /**
   * 
   */
  public ValidatableSentence(Sentence st) {
    super(st);
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    return this.printStatus("TextSpan must be set", this.annotation.isSetTextSpan())
        && new ValidatableTextSpan(this.annotation.getTextSpan()).validate(c);
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    return this.validateUUID(this.annotation.getUuid())
        // TODO: Change hard coded TextSpan to consider AudioSpan if it exists.
        && this.printStatus("TextSpan must be set", this.annotation.isSetTextSpan());
  }

}
