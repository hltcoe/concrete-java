/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.concurrent;

import java.util.concurrent.Callable;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 *
 */
public class CallableCommunicationToBytes implements Callable<byte[]> {

  private static final CommunicationSerializer cs = new CompactCommunicationSerializer();

  private final Communication c;

  /**
   *
   */
  public CallableCommunicationToBytes(Communication c) {
    this.c = c;
  }

  /* (non-Javadoc)
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public byte[] call() throws ConcreteException {
    return cs.toBytes(this.c);
  }
}
