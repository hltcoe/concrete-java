/*
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.TokenRefSequence;

/**
 * @author max
 *
 */
public class ValidatableEntityMention extends AbstractAnnotation<EntityMention> {

  /**
   * @param annotation
   */
  public ValidatableEntityMention(EntityMention annotation) {
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
    boolean emValid = this.validateUUID(this.annotation.getUuid())
        && this.printStatus("EntityType must be set", this.annotation.isSetEntityType())
        && this.printStatus("PhraseType must be set", this.annotation.isSetPhraseType())
        && this.printStatus("Tokens must be set", this.annotation.isSetTokens());
    if (!emValid)
      return false;
    else {
      TokenRefSequence trs = this.annotation.getTokens();
      return new ValidatableTokenRefSequence(trs).isValid();
    }
  }

}
