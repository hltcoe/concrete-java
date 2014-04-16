/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.TextSpan;

/**
 * @author max
 *
 */
public class ValidatableTextSpan extends AbstractAnnotation<TextSpan> {

  public ValidatableTextSpan(TextSpan annotation) {
    super(annotation);
  }

  @Override
  public boolean isValid(Communication c) {
    int begin = this.getAnnotation().start;
    int end = this.getAnnotation().ending;
    int textLength = c.getText().length();
    
    // Begin can't be negative
    // End can't be equal or less than beginning
    // End can't be > length of text
    return begin >= 0 
        && end > begin 
        && end <= textLength;
  }

  @Override
  public boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }
}
