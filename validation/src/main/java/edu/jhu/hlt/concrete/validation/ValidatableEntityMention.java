/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
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
    TokenRefSequence trs = this.annotation.getTokens();
    return this.printStatus("TokenRefSeq must be valid with given Communication", new ValidatableTokenRefSequence(trs).isValidWithComm(c));
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
      return this.printStatus("TokenRefSeq must be valid", new ValidatableTokenRefSequence(trs).isValid());
    }
  }
}
