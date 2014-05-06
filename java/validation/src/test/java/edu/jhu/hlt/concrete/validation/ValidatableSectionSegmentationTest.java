/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.TextSpan;

/**
 * @author max
 *
 */
public class ValidatableSectionSegmentationTest extends AbstractValidationTest {

  private static final XLogger logger = XLoggerFactory.getXLogger(ValidatableSectionSegmentationTest.class);
  
  SectionSegmentation ss;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    ss = this.generateValidSectSeg(this.comm);
  }
  
  private void test(boolean checkTrue) {
    if (checkTrue)
      assertTrue(new ValidatableSectionSegmentation(this.ss).validate(this.comm));
    else
      assertFalse(new ValidatableSectionSegmentation(this.ss).validate(this.comm));
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }
  
  public SectionSegmentation generateValidSectSeg() {
    return generateValidSectSeg(this.factory.randomCommunication());
  }
  
  public SectionSegmentation generateValidSectSeg (Communication c) {
    Communication copy = new Communication(c);
    
    SectionSegmentation ss = new SectionSegmentation();
    ss.uuid = UUID.randomUUID().toString();
    String commText = copy.getText();
    
    Section s = new Section();
    s.uuid = UUID.randomUUID().toString();
    s.kind = "Other";
    s.textSpan = new TextSpan(0, commText.length());
    
    ss.addToSectionList(s);
    ss.metadata = this.factory.randomMetadata();
    return ss;
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void good() {
    logger.entry();
    this.test(true);
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void badUUID() {
    this.ss.uuid = "hello";
    this.test(false);
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void noUUID() {
    ss.uuid = null;
    this.test(false);
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void noSections() {
    ss.getSectionList().remove(0);
    this.test(false);
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValidWithComm(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void invalidTextSpan() {
    Section s = ss.getSectionList().get(0);
    s.setTextSpan(new TextSpan(-1, 1));
    this.test(false);
  }
}
