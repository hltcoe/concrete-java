/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import org.junit.After;
import org.junit.Before;

import edu.jhu.hlt.concrete.random.RandomConcreteFactory;

/**
 * @author max
 *
 */
public class SuperCommunicationTest {

  RandomConcreteFactory cf;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    cf = new RandomConcreteFactory();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }


//  @Test
//  public void entityMentionSetCaching() {
//    Communication c = new ConcreteFactory().randomCommunication();
//    EntityMentionSet ems = new EntityMentionSet();
//    ems.setUuid(new ConcreteUUIDFactory().getConcreteUUID());
//
//  }
}
