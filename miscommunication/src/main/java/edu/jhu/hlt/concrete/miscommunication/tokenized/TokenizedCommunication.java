/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.tokenized;

import java.util.List;

import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.miscommunication.sentenced.SentencedCommunication;

/**
 *
 */
public interface TokenizedCommunication extends SentencedCommunication {
  public List<Tokenization> getTokenizations();
  /**
   * @return a {@link List} of all {@link Token}s in this TokenizedCommunication
   */
  public List<Token> getTokens();
}
