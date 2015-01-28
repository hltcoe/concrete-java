/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.concurrent;

import java.util.concurrent.Callable;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class CallableCommunicationToBytes implements Callable<byte[]> {

  private final CommunicationSerializer cs;
  private final Communication c;
  
  /**
   * 
   */
  public CallableCommunicationToBytes(Communication c, CommunicationSerializer cs) {
    this.c = c;
    this.cs = cs;
  }

  /* (non-Javadoc)
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public byte[] call() throws ConcreteException {
    return this.cs.toBytes(this.c);
  }
}
