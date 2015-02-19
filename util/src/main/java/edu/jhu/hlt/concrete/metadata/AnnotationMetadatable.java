/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.metadata;

import edu.jhu.hlt.concrete.AnnotationMetadata;

/**
 * Interface whose contract allows consumers to obtain {@link AnnotationMetadata}
 * about the implementation.
 */
public interface AnnotationMetadatable {
  /**
   * @return the {@link AnnotationMetadata} associated with this object
   */
  public AnnotationMetadata getMetadata();
}
