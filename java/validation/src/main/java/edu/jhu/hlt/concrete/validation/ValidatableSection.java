/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;

/**
 * @author max
 *
 */
public class ValidatableSection extends AbstractAnnotation<Section> {

  /**
   * @param annotation
   */
  public ValidatableSection(Section annotation) {
    super(annotation);
  }

  @Override
  public boolean isValid(Communication c) {
    return false;
  }
  
  public boolean validSections() {
    return false;
  }

  @Override
  public boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }
}
