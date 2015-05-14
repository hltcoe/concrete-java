/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sentenced;

import java.util.List;

import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.miscommunication.sectioned.SectionedCommunication;

/**
 *
 */
public interface SentencedCommunication extends SectionedCommunication {
  public List<Sentence> getSentences();
}
