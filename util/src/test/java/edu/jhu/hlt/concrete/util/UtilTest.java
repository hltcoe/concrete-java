/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * @author max
 *
 */
public class UtilTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * 
   */
  @Test
  public void good() {
    assertTrue(UUIDFactory.isValidUUID(UUID.randomUUID().toString()));
  }

  /**
   * 
   */
  @Test
  public void bad() {
    assertFalse(UUIDFactory.isValidUUID("asdf-4124-fa"));
  }

  /**
   * 
   */
  @Test
  public void bad36() {
    assertFalse(UUIDFactory.isValidUUID("asdf----ffffyyyyhfgb4123namd40ol41nv"));
  }

  /**
   * 
   */
  @Test
  public void badNoHyphen() {
    assertFalse(UUIDFactory.isValidUUID("o3ogjot3jto3jt"));
  }
}
