/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.lemma;

import java.util.List;

import edu.jhu.hlt.concrete.TokenTagging;

/**
 *
 */
public interface LemmatizedCommunication {
  public List<TokenTagging> getLemmaTaggings();
}
