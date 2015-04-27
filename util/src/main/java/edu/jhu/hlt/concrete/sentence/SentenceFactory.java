/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.sentence;

import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 *
 */
public class SentenceFactory {

  /**
   * 
   */
  private SentenceFactory() {
    // TODO Auto-generated constructor stub
  }
  
  public static final Sentence create() {
    return new Sentence()
      .setUuid(UUIDFactory.newUUID());
  }
}
