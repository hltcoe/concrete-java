/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import java.util.Iterator;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Tokenization;
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
  protected boolean isValidWithComm(Communication c) {
    return true;
  }

  @Override
  public boolean isValid() {
    boolean initValidation = 
        this.printStatus("Metadata must be set", this.annotation.isSetMetadata())
        && this.printStatus("Metadata must be valid", new ValidatableMetadata(this.annotation.getMetadata()).isValid())
        // Sections must exist.
        && this.printStatus("Sections must exist", this.annotation.isSetTokenizationList())
        // Section size must be >0.
        && this.printStatus("Section size must be >0", this.annotation.getTokenizationListSize() > 0);
    if (initValidation) {
      Iterator<Tokenization> tokIter = this.annotation.getTokenizationListIterator();
      boolean subValid = true;
      while (subValid && tokIter.hasNext())
        subValid = new ValidatableTokenization(tokIter.next()).isValid();
      return subValid;
    } else
      return false;
  }
}
