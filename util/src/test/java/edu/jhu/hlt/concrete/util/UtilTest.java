/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
   * Test method for {@link edu.jhu.hlt.concrete.util.Util#isValidUUIDString(java.lang.String)}.
   */
  @Test
  public void good() {
    assertTrue(Util.isValidUUIDString(UUID.randomUUID().toString()));
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.util.Util#isValidUUIDString(java.lang.String)}.
   */
  @Test
  public void bad() {
    assertFalse(Util.isValidUUIDString("asdf-4124-fa"));
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.util.Util#isValidUUIDString(java.lang.String)}.
   */
  @Test
  public void bad36() {
    assertFalse(Util.isValidUUIDString("asdf----ffffyyyyhfgb4123namd40ol41nv"));
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.util.Util#isValidUUIDString(java.lang.String)}.
   */
  @Test
  public void badNoHyphen() {
    assertFalse(Util.isValidUUIDString("o3ogjot3jto3jt"));
  }
}
