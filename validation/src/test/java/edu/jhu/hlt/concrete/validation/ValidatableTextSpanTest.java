/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.TextSpan;

/**
 * @author max
 *
 */
public class ValidatableTextSpanTest extends AbstractValidationTest {

  TextSpan base;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    base = new TextSpan(0, this.comm.text.length());
  }

  private boolean testValidity () {
    return new ValidatableTextSpan(this.base).validate(this.comm);
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void good() {
    assertTrue(this.testValidity());
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void negativeStart() {
    this.base.setStart(-1);
    assertFalse(this.testValidity());
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void negativeEnd() {
    this.base.setEnding(-1);
    assertFalse(this.testValidity());
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void endLessThanStart() {
    this.base.setStart(2);
    this.base.setEnding(1);
    assertFalse(this.testValidity());
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void endGreaterThanLength() {
    this.base.setEnding(this.comm.getText().length() + 2);
    assertFalse(this.testValidity());
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void zeroLength() {
    this.base.setStart(5);
    this.base.setEnding(5);
    assertFalse(this.testValidity());
  }
}
