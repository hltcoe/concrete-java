/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;

/**
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
    return this.printStatus("Tool must be set", this.annotation.isSetTool())
        && this.printStatus("Timestamp must be set", this.annotation.isSetTimestamp());
  }
}
