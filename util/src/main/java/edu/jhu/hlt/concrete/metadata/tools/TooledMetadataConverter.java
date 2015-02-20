/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.metadata.tools;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.TheoryDependencies;

/**
 * Class that converts {@link SafeTooledAnnotationMetadata} objects
 * into {@link AnnotationMetadata} objects.
 */
public class TooledMetadataConverter {

  private TooledMetadataConverter() {

  }

  /**
   * Produce an {@link AnnotationMetadata} object with required fields set. This
   * can then be appended to, with {@link TheoryDependencies} and such, producing the
   * complete object.
   *
   * @param md the {@link SafeTooledAnnotationMetadata} from which to build the metadata
   * @return an {@link AnnotationMetadata} that will not produce write-time errors
   */
  public static final AnnotationMetadata convert(SafeTooledAnnotationMetadata md) {
    return new AnnotationMetadata()
        .setTool(md.getTool())
        .setTimestamp(md.getTimestamp());
  }
}
