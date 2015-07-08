/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.entitied;

import java.util.List;

import edu.jhu.hlt.concrete.EntityMention;
import edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication;

/**
 *
 */
public interface EntityMentionedCommunication extends TokenizedCommunication {
  public List<EntityMention> getEntityMentions();
}
