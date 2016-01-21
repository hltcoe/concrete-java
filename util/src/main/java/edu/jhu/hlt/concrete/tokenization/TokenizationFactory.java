/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 *
 */
public class TokenizationFactory {

  private final AnalyticUUIDGenerator gen;

  /**
   *
   */
  private TokenizationFactory(final AnalyticUUIDGenerator gen) {
    this.gen = gen;
  }

  public Tokenization create() {
    return new Tokenization()
      .setUuid(this.gen.next());
  }
}
