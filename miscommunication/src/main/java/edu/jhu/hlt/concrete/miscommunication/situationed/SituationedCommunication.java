/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.situationed;

import java.util.List;

import edu.jhu.hlt.concrete.Situation;
import edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication;

/**
 *
 */
public interface SituationedCommunication extends TokenizedCommunication {
  public List<Situation> getSituations();
}
