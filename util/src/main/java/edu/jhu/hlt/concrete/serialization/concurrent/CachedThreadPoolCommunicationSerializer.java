/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization.concurrent;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.concurrent.CallableBytesToCommunication;
import edu.jhu.hlt.concrete.concurrent.CallableCommunicationToBytes;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class CachedThreadPoolCommunicationSerializer implements AsyncCommunicationSerializer, 
    AutoCloseable {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CachedThreadPoolCommunicationSerializer.class);
  
  private final ExecutorService exec;
  private final CompletionService<byte[]> bcs;
  private final CompletionService<Communication> ccs;
  
  private final CommunicationSerializer cs = new CompactCommunicationSerializer();
  
  /**
   * 
   */
  public CachedThreadPoolCommunicationSerializer() {
    this.exec = Executors.newCachedThreadPool();
    this.bcs = new ExecutorCompletionService<byte[]>(this.exec);
    this.ccs = new ExecutorCompletionService<Communication>(this.exec);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.AsyncCommunicationSerializer#toBytes(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public Future<byte[]> toBytes(Communication c) throws ConcreteException {
    return this.bcs.submit(new CallableCommunicationToBytes(c, this.cs));
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.AsyncCommunicationSerializer#fromBytes(byte[])
   */
  @Override
  public Future<Communication> fromBytes(byte[] bytes) throws ConcreteException {
    return this.ccs.submit(new CallableBytesToCommunication(bytes));
  }

  /* (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws InterruptedException {
    LOGGER.info("Close called. Shutting down and awaiting task completion.");
    this.exec.shutdown();
    this.exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    LOGGER.info("Shutdown complete.");
  }
}
