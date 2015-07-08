/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.depparsed;

import java.util.List;

import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication;

/**
 *
 */
public interface DependencyParsedCommunication extends TokenizedCommunication {
  public List<DependencyParse> getDependencyParses();
}
