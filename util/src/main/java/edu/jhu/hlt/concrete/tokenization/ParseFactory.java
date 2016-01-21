/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 *
 */
public class ParseFactory {

  private final AnalyticUUIDGenerator gen;

  public ParseFactory(final AnalyticUUIDGenerator gen) {
    this.gen = gen;
  }

  /**
   * @return a {@link Parse} with a {@link UUID} set
   */
  public final Parse create() {
    return new Parse().setUuid(this.gen.next());
  }
}
