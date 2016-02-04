/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import java.util.ArrayList;

import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 *
 */
public class TokenTaggingFactory {
  private final AnalyticUUIDGenerator gen;
  /**
   *
   */
  public TokenTaggingFactory(final AnalyticUUIDGenerator gen) {
    this.gen = gen;
  }

  /**
   *
   * @return a {@link TokenTagging} with a {@link UUID} set
   */
  public final TokenTagging create() {
    return new TokenTagging().setUuid(this.gen.next())
        .setTaggedTokenList(new ArrayList<>());
  }

  /**
   *
   * @param tokenTaggingType the type that will be set on the produced {@link TaggedToken} object
   * @return a {@link TokenTagging} with both a {@link UUID} and a taggingType set
   */
  public final TokenTagging create(final String tokenTaggingType) {
    return create()
        .setTaggingType(tokenTaggingType);
  }
}
