/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.validation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.communications.SuperCommunication;

/**
 * @author max
 *
 */
public class ValidatableEntity extends AbstractAnnotation<Entity> {

  private final Set<UUID> entityMentionUUIDSet;
  
  /**
   * @param annotation
   */
  public ValidatableEntity(Entity annotation) {
    super(annotation);
    this.entityMentionUUIDSet = new HashSet<>();
  }
  
  public ValidatableEntity(Entity annotation, Set<UUID> entityMentionUUIDSet) {
    super(annotation);
    this.entityMentionUUIDSet = new HashSet<>(entityMentionUUIDSet);
  }
  
  

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    if (this.entityMentionUUIDSet.isEmpty()) {
      SuperCommunication sc = new SuperCommunication(c);
      Map<UUID, EntityMention> idToMenMap = sc.generateEntityMentionIdToEntityMentionMap();
      Set<UUID> emsIdSet = idToMenMap.keySet();
      this.entityMentionUUIDSet.addAll(emsIdSet);
    }
      
    Iterator<UUID> mentionUUIDIter = this.annotation.getMentionIdListIterator();
    boolean valid = true;
    while (valid && mentionUUIDIter.hasNext()) {
      UUID ptr = mentionUUIDIter.next();
      valid = this.entityMentionUUIDSet.contains(ptr);
    }
    
    return valid;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    return this.validateUUID(this.annotation.getUuid())
        && this.printStatus("EntityMentions must be set", this.annotation.isSetMentionIdList())
        && this.printStatus("EntityMentions must be >0", this.annotation.getMentionIdListSize() > 0)
        && this.printStatus("Type must be set", this.annotation.isSetType())
        // TODO: truly validate below
        && this.printStatus("Type must be recognized", !this.annotation.getType().isEmpty()); 
  }

}
