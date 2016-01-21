/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * Utility class for easier creation of Concrete
 * {@link DependencyParse} objects.
 */
public class DependencyParseFactory {

  private final AnalyticUUIDGenerator gen;

  public DependencyParseFactory(final AnalyticUUIDGenerator gen) {
    this.gen = gen;
  }

  /**
   * @return a {@link DependencyParse} with a Concrete {@link UUID} set
   */
  public final DependencyParse create() {
    return new DependencyParse()
        .setUuid(this.gen.next());
  }
}
