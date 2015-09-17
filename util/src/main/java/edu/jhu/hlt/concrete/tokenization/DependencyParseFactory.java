/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * Utility class for easier creation of Concrete
 * {@link DependencyParse} objects.
 */
public class DependencyParseFactory {
  /**
   *
   * @return a {@link DependencyParse} with a Concrete {@link UUID} set
   */
  public static final DependencyParse create() {
    return new DependencyParse()
        .setUuid(UUIDFactory.newUUID());
  }
}
