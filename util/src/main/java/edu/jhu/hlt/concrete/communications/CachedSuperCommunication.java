/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.communications;

import edu.jhu.hlt.concrete.Communication;

/**
 * Aggressively cached version of {@link SuperCommunication}, plus bonus
 * utilities. 
 * 
 * @author max
 */
public class CachedSuperCommunication extends TokenizedSuperCommunication {

  /**
   * Single arg ctor: pass in a {@link Communication} to wrap and cache.
   */
  public CachedSuperCommunication(Communication comm) {
    super(comm);
  }
}
