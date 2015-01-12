/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import static org.junit.Assert.fail;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import concrete.util.data.ConcreteFactory;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class CachedThreadPoolCommunicationSerializerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CachedThreadPoolCommunicationSerializerTest.class);
  
  private final ConcreteFactory cf = new ConcreteFactory(1234L);
  private final Set<Communication> commSet = this.cf.randomCommunicationSet(100 * 1000);
  private final CommunicationSerializer cs = new ThreadSafeCompactCommunicationSerializer();
  
  private AsyncCommunicationSerializer ser;
  
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    this.ser = new CachedThreadPoolCommunicationSerializer();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    this.ser.close();
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.concrete.serialization.CachedThreadPoolCommunicationSerializer#toBytes(edu.jhu.hlt.concrete.Communication)}.
   * @throws ConcreteException 
   * @throws ExecutionException 
   * @throws InterruptedException 
   */
  @Test
  public void testToBytes() throws ConcreteException, InterruptedException, ExecutionException {
    Deque<Future<byte[]>> dq = new ArrayDeque<>(100 * 1000 + 1);
    LOGGER.info("Adding communications to set.");
    for (Communication c : this.commSet)
      dq.addLast(this.ser.toBytes(c));
    LOGGER.info("Communications added.");
    while (!dq.isEmpty()) {
      Future<byte[]> fb = dq.pop();
      byte[] ba = fb.get();
      this.cs.fromBytes(ba).getId();
    }
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.serialization.CachedThreadPoolCommunicationSerializer#fromBytes(byte[])}.
   * @throws ConcreteException 
   * @throws ExecutionException 
   * @throws InterruptedException 
   */
  @Test
  public void testFromBytes() throws ConcreteException, InterruptedException, ExecutionException {
    Deque<Future<Communication>> dq = new ArrayDeque<>(100 * 1000 + 1);
    LOGGER.info("Adding communications to set.");
    for (Communication c : this.commSet) {
      byte[] bs = this.cs.toBytes(c);
      dq.addLast(this.ser.fromBytes(bs));
    }
    
    LOGGER.info("Communications added.");
    while (!dq.isEmpty()) {
      Future<Communication> fb = dq.pop();
      fb.get().getId();
    }
  }
}
