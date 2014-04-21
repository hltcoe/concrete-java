/*
 * 
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
   * @param comm
   */
  public CachedSuperCommunication(Communication comm) {
    super(comm);
  }
}
