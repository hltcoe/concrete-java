/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.pos;

import java.util.List;

import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication;

/**
 *
 */
public interface PartOfSpeechTaggedCommunication extends TokenizedCommunication {
  public List<TokenTagging> getPOSTaggings(); 
}
