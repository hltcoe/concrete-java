/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.safe.metadata;

import edu.jhu.hlt.concrete.AnnotationMetadata;

/**
 * Interface for consumers that can produce {@link AnnotationMetadata} objects.
 */
public interface Metadatable {
  /**
   * @return the {@link SafeAnnotationMetadata}
   */
  public SafeAnnotationMetadata getMetadata();
}
