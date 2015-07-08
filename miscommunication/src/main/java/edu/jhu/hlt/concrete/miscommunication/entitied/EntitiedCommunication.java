/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.entitied;

import java.util.List;

import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication;

/**
 *
 */
public interface EntitiedCommunication extends TokenizedCommunication {
  public List<Entity> getEntities();
}
