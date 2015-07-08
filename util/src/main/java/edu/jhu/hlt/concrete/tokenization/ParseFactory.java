/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 *
 */
public class ParseFactory {

  /**
   *
   */
  private ParseFactory() {
    // TODO Auto-generated constructor stub
  }

  /**
   *
   * @return a {@link Parse} with a {@link UUID} set
   */
  public static final Parse create() {
    return new Parse().setUuid(UUIDFactory.newUUID());
  }
}
