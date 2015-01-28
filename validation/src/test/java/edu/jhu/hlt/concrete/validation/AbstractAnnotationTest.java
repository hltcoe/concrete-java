/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.AnnotationMetadata;

/**
 * @author max
 *
 */
public class AbstractAnnotationTest {

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
   * Test method for {@link edu.jhu.hlt.concrete.validation.AbstractAnnotation#getAnnotation()}.
   */
  @Test
  public void testGetAnnotation() {
    AnnotationMetadata md = new AnnotationMetadata()
      .setTool("Test")
      .setTimestamp(System.currentTimeMillis());
    ValidatableMetadata vmd = new ValidatableMetadata(md);
    assertEquals("Tools should be equal.", md.getTool(), vmd.getAnnotation().getTool());
    md.setTool("Test-again");
    assertNotEquals("After change to seed object, tools should not be equal.", md.getTool(), vmd.getAnnotation().getTool());
    String test = "Test-thrice";
    vmd.getAnnotation().setTool(test);
    assertNotEquals("After change to fetched object, tools should not be equal.", test, vmd.getAnnotation().getTool());
  }

}
