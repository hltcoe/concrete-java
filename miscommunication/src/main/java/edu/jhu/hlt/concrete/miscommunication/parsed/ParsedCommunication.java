/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.parsed;

import java.util.List;

import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication;

/**
 *
 */
public interface ParsedCommunication extends TokenizedCommunication {
  public List<Parse> getParses();
}
