/*
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.SituationMention;

/**
 * @author max
 *
 */
public class ValidatableSituationMention extends AbstractAnnotation<SituationMention> {

  /**
   * @param annotation
   */
  public ValidatableSituationMention(SituationMention annotation) {
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
    boolean init = this.validateUUID(this.annotation.getUuid())
        && this.printStatus("ArgumentList must be set", this.annotation.isSetArgumentList())
        && this.printStatus("Argument list must not be empty", this.annotation.getArgumentListSize() > 0)
        && this.printStatus("SituationType must be set", this.annotation.isSetSituationType());
    if (!init) return false;
    else {
      String st = this.annotation.getSituationType();
      if (st == "Event")
        return this.printStatus("If SituationType == EVENT, EventType must be set.", this.annotation.isSetEventType());
      else if (st == "State")
        return this.printStatus("If SituationType == STATE, StateType must be set.", this.annotation.isSetStateType());
      else
        return true;
    }
  }
}
