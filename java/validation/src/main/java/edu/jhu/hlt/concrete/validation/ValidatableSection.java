/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.util.Util;

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
  protected boolean isValidWithComm(Communication c) {
    // No text --> false
    if (!this.printStatus("Text must be set in the comm", c.isSetText())) return false;
    // For Sections: need to ensure that this TextSpan
    // is valid in the context of the communication.
    return this.printStatus("TextSpan must be valid wrt comm", new ValidatableTextSpan(this.annotation.getTextSpan()).isValidWithComm(c));
  }

  @Override
  public boolean isValid() {
    return this.printStatus("UUID must be valid", Util.isValidUUIDString(this.annotation.getUuid()))
        // Hard-coded to text validity. 
        // TODO: update to Audio if ever is used.
        && this.printStatus("SectionKind must be set", this.annotation.isSetKind())
        && this.printStatus("TextSpan must be set", this.annotation.isSetTextSpan())
        && this.printStatus("TextSpan must be valid", new ValidatableTextSpan(this.annotation.getTextSpan()).isValid());
  }
}
