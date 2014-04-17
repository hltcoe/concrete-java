/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;

/**
 * @author max
 *
 */
public class ValidatableSectionTest extends AbstractValidationTest {
  
  // private ValidatableSection vs;
  
  public class ValidatableSection extends AbstractAnnotation<Section> {

    public ValidatableSection(Section annotation) {
      super(annotation);
    }

    @Override
    public boolean isValidWithComm(Communication c) {
      return false;
    }

    @Override
    public boolean isValid() {
      // TODO Auto-generated method stub
      return false;
    }
    
  }
  
  public Section generateSection() {
    return null;
  }

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

  @Test
  public void test() {
    
  }

}
