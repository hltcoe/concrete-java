/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 *
 */
public class TokenTaggingFactory {

  /**
   *
   */
  private TokenTaggingFactory() {
    // TODO Auto-generated constructor stub
  }

  public static final TokenTagging create() {
    return new TokenTagging().setUuid(UUIDFactory.newUUID());
  }
}
