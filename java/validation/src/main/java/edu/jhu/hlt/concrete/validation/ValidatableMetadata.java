/*
 * 
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;

/**
 * @author max
 *
 */
public class ValidatableMetadata extends AbstractAnnotation<AnnotationMetadata> {

  /**
   * @param annotation
   */
  public ValidatableMetadata(AnnotationMetadata annotation) {
    super(annotation);
  }

  @Override
  protected boolean isValidWithComm(Communication c) {
    // Metadata is independent of the attached communication
    return this.isValid();
  }

  @Override
  public boolean isValid() {
    // Rebar requires tool names and timestamps.
    return this.printStatus("Tool must be set", this.annotation.isSetTool())
        && this.printStatus("Timestamp must be set", this.annotation.isSetTimestamp());
  }
}
