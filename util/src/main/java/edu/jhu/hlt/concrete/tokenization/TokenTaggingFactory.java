/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import java.util.ArrayList;

import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.UUID;
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

  /**
   *
   * @return a {@link TokenTagging} with a {@link UUID} set
   */
  public static final TokenTagging create() {
    return new TokenTagging().setUuid(UUIDFactory.newUUID())
        .setTaggedTokenList(new ArrayList<>());
  }

  /**
   *
   * @param tokenTaggingType the type that will be set on the produced {@link TaggedToken} object
   * @return a {@link TokenTagging} with both a {@link UUID} and a taggingType set
   */
  public static final TokenTagging create(final String tokenTaggingType) {
    return create()
        .setTaggingType(tokenTaggingType);
  }
}
