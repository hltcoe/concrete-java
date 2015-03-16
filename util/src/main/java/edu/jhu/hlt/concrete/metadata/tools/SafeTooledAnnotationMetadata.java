/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.metadata.tools;

import edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata;

/**
 * Interface that extends the functionality of {@link SafeAnnotationMetadata},
 * requiring implementation a {@link MetadataTool}.
 * <br>
 * <br>
 * Additionally, contains a default implementation of {@link SafeAnnotationMetadata#getTool()}
 * that uses the provided MetadataTool to create a well-formed Tool string.
 */
public interface SafeTooledAnnotationMetadata extends SafeAnnotationMetadata, MetadataTool {

  /**
   * By default, use {@link NewlineMetadataToolBuilder#generateToolString(MetadataTool)}
   * to generate a parseable Tool string. Implementers should generally not override
   * this implementation.
   */
  @Override
  default String getTool() {
    return NewlineMetadataToolBuilder.generateToolString(this);
  }
}
