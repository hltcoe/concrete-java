/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import concrete.util.concurrent.CallableBytesToCommunication;
import concrete.util.concurrent.CallableCommunicationToBytes;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class CachedThreadPoolCommunicationSerializer implements AsyncCommunicationSerializer, AutoCloseable {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CachedThreadPoolCommunicationSerializer.class);
  
  private final ExecutorService exec;
  private final CompletionService<byte[]> bcs;
  private final CompletionService<Communication> ccs;
  
  private final CommunicationSerializer cs = new ThreadSafeCompactCommunicationSerializer();
  
  /**
   * 
   */
  public CachedThreadPoolCommunicationSerializer() {
    this.exec = Executors.newCachedThreadPool();
    this.bcs = new ExecutorCompletionService<byte[]>(this.exec);
    this.ccs = new ExecutorCompletionService<Communication>(this.exec);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
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
  public void close() throws Exception {
    LOGGER.info("Close called.");
    this.exec.shutdown();
    LOGGER.info("Shutdown complete.");
  }

}
