/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.sentence;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 *
 */
public class SentenceFactory {

  private final AnalyticUUIDGenerator gen;
  /**
   *
   */
  public SentenceFactory(final AnalyticUUIDGenerator gen) {
    this.gen = gen;
  }

  public SentenceFactory(final Communication comm) {
    this.gen = new AnalyticUUIDGeneratorFactory(comm).create();
  }

  public final Sentence create() {
    return new Sentence()
      .setUuid(this.gen.next());
  }
}
