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

  // Beginning.
  private final int begin;
  // End.
  private final int end;
  
  public ValidatableTextSpan(TextSpan annotation) {
    super(annotation);
    this.begin = this.annotation.getStart();
    this.end = this.annotation.getEnding();
  }

  @Override
  protected boolean isValidWithComm(Communication c) {
    int textLength = c.getText().length();
    
    // End can't be > length of text
    return this.printStatus("End can't be > text length", end <= textLength);
  }

  @Override
  public boolean isValid() {
    return this.printStatus("Beginning has to be >= 0", begin >= 0)
        && this.printStatus("Ending has to be > beginning", end > begin);
  }
}
