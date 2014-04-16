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

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionKind;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.TextSpan;

/**
 * @author max
 *
 */
public class ValidatableSectionSegmentationTest extends AbstractValidationTest {

  ValidatableSectionSegmentation vss;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    SectionSegmentation ss = this.generateValidSectSeg(this.comm);
    this.vss = new ValidatableSectionSegmentation(ss);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }
  
  public SectionSegmentation generateValidSectSeg() {
    return generateValidSectSeg(generateValidCommunication());
  }
  
  public SectionSegmentation generateValidSectSeg (Communication c) {
    Communication copy = new Communication(c);
    
    SectionSegmentation ss = new SectionSegmentation();
    ss.uuid = UUID.randomUUID().toString();
    String commText = copy.getText();
    
    Section s = new Section();
    s.uuid = UUID.randomUUID().toString();
    s.kind = SectionKind.OTHER;
    s.textSpan = new TextSpan(0, commText.length());
    
    ss.addToSectionList(s);
    ss.metadata = this.getMetadata();
    return ss;
  }

  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void good() {
    assertTrue(this.vss.isValid(this.comm));
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void badUUID() {
    SectionSegmentation ss = this.vss.getAnnotation();
    ss.uuid = "hello";
    assertFalse(this.vss.isValid(this.comm));
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void noUUID() {
    SectionSegmentation ss = this.vss.getAnnotation();
    ss.uuid = null;
    assertFalse(this.vss.isValid(this.comm));
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void noSections() {
    SectionSegmentation ss = this.vss.getAnnotation();
    ss.getSectionList().remove(0);
    assertFalse(this.vss.isValid(this.comm));
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.ballast.validation.ValidatableSectionSegmentation#isValid(edu.jhu.hlt.concrete.Communication)}.
   */
  @Test
  public void invalidTextSpan() {
    SectionSegmentation ss = this.vss.getAnnotation();
    Section s = ss.getSectionList().get(0);
    s.setTextSpan(new TextSpan(-1, 1));
    assertFalse(this.vss.isValid(this.comm));
  }
}
