/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.validation;

import java.util.Iterator;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.EntityMentionSet;
import edu.jhu.hlt.concrete.UUID;

/**
 * @author max
 *
 */
public class ValidatableEntityMentionSet extends AbstractAnnotation<EntityMentionSet> {

  /**
   * @param annotation
   */
  public ValidatableEntityMentionSet(EntityMentionSet annotation) {
    super(annotation);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    boolean validMens = true;
    Iterator<EntityMention> menIter = this.annotation.getMentionSetIterator();
    while (validMens && menIter.hasNext()) {
      EntityMention em = menIter.next();
      validMens = this.printStatus("EntityMention: " + em.getUuid().toString() + " must be valid", new ValidatableEntityMention(em).validate(c));
    }
    
    return validMens;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    boolean init = this.validateUUID(this.annotation.getUuid())
        && this.printStatus("Metadata must be valid", new ValidatableMetadata(this.annotation.getMetadata()).isValid())
        && this.printStatus("EntityMentionSet must be set.", this.annotation.isSetMentionSet());
    if (init) {
      boolean validMens = true;
      Iterator<EntityMention> menIter = this.annotation.getMentionSetIterator();
      while (validMens && menIter.hasNext()) {
        EntityMention em = menIter.next();
        UUID emId = em.getUuid();
        validMens = this.printStatus("EntityMention: " + emId.toString() + " must be valid.", new ValidatableEntityMention(em).isValid());
      }
      
      return validMens;
    } else
      return false;
  }

}
