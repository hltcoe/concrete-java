/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.communications;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * Class that allows for construction of semi-built {@link Communication}
 * objects.
 */
@Deprecated
public class CommunicationFactory {

  private final AnalyticUUIDGenerator gen;

  public CommunicationFactory() {
    this.gen = new AnalyticUUIDGeneratorFactory().create();
  }

  /**
   * @return a {@link Communication} with a {@link UUID} assigned.
   */
  public final Communication create() {
    return new Communication()
      .setUuid(this.gen.next());
  }
}
