/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 *
 */
public class TokenizationFactory {

  /**
   * 
   */
  private TokenizationFactory() {
    // TODO Auto-generated constructor stub
  }
  
  public static Tokenization create() {
    return new Tokenization()
      .setUuid(UUIDFactory.newUUID());
  }
}
