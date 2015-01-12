/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package concrete.util.concurrent;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class CallableCommunicationToBytes implements Callable<byte[]> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CallableCommunicationToBytes.class);
  
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
    // LOGGER.debug("Serializing comm: {}", c.getId());
    return this.cs.toBytes(this.c);
  }
}
