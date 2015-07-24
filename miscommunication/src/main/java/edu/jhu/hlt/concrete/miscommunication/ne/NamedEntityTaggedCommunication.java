/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.ne;

import java.util.List;

import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.miscommunication.tokenized.TokenizedCommunication;

/**
 *
 */
public interface NamedEntityTaggedCommunication extends TokenizedCommunication {
  public List<TokenTagging> getNETaggings();
}
