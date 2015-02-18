/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.TextSpan;

/**
 * @author max
 *
 */
public class SuperTextSpanTest {

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
   * Test method for {@link edu.jhu.hlt.concrete.util.SuperTextSpan#getText()}.
   */
  @Test
  public void testGetText() {
    Communication c = new Communication();
    String txt = "Test out super text spans!";
    String txtTwo = "More test text";
    c.setText(txt + txtTwo);
    TextSpan ts = new TextSpan(0, txt.length());
    SuperTextSpan sts = new SuperTextSpan(ts, c);
    assertEquals(txt, sts.getText());
    assertEquals(txtTwo, new SuperTextSpan(new TextSpan(txt.length(), txt.length() + txtTwo.length()), c).getText());
  }

}
