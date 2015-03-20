/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.concurrent;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.communications.SuperCommunication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;

/**
 * @author max
 *
 */
public class CallableBytesToConcreteFile implements Callable<Void> {

  private final CommunicationSerializer cs = new CompactCommunicationSerializer();
  private final byte[] bytes;
  private final Path outPath;
  
  public CallableBytesToConcreteFile (byte[] bytes, Path outPath) {
    this.bytes = bytes;
    this.outPath = outPath;
  }
  
  /* (non-Javadoc)
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public Void call() throws Exception {
    Communication c = cs.fromBytes(this.bytes);
    new SuperCommunication(c).writeToFile(this.outPath, true);
    return null;
  }
}
