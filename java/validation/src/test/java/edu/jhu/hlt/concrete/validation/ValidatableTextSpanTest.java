/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.TextSpan;

/**
 * @author max
 *
 */
public class ValidatableTextSpanTest extends AbstractValidationTest {

  ValidatableTextSpan vts;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    TextSpan good = new TextSpan(0, this.comm.text.length());
    this.vts = new ValidatableTextSpan(good);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void good() {
    assertTrue(this.vts.isValid(this.comm));
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void negativeStart() {
    this.vts.getAnnotation().start = -1;
    assertFalse(this.vts.isValid(this.comm));
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void negativeEnd() {
    this.vts.getAnnotation().ending = -1;
    assertFalse(this.vts.isValid(this.comm));
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void endLessThanStart() {
    this.vts.getAnnotation().start = 4;
    this.vts.getAnnotation().ending = 3;
    assertFalse(this.vts.isValid(this.comm));
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void endGreaterThanLength() {
    this.vts.getAnnotation().ending = this.comm.getText().length() + 100;
    assertFalse(this.vts.isValid(this.comm));
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableTextSpan#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void zeroLength() {
    this.vts.getAnnotation().start = 5;
    this.vts.getAnnotation().ending = 5;
    assertFalse(this.vts.isValid(this.comm));
  }
}
