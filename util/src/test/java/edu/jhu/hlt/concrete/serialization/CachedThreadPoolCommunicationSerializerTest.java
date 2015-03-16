/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.random.RandomConcreteFactory;
import edu.jhu.hlt.concrete.serialization.concurrent.AsyncCommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.concurrent.CachedThreadPoolCommunicationSerializer;

/**
 * @author max
 *
 */
public class CachedThreadPoolCommunicationSerializerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CachedThreadPoolCommunicationSerializerTest.class);

  private final RandomConcreteFactory cf = new RandomConcreteFactory(1234L);
  private final Set<Communication> commSet = this.cf.communicationSet(100 * 1000);
  private final CommunicationSerializer cs = new CompactCommunicationSerializer();

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
   * @throws java.lang.Exception
   */
  @Test
  public void testToBytes() throws Exception {
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
   * @throws java.lang.Exception
   */
  @Test
  public void testFromBytes() throws Exception {
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
