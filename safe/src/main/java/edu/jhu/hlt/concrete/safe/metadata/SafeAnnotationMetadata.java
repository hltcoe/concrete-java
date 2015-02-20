/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.safe.metadata;

import edu.jhu.hlt.concrete.AnnotationMetadata;


/**
 * Interface whose contract contains the required fields of {@link AnnotationMetadata}.
 * <br/>
 * <br/>
 * This interface exists to avoid "write time" errors that can occur when {@link AnnotationMetadata}
 * is present, but the required fields on the object are unset. Utilizing this interface allows
 * compile-time detection of these errors.
 */
public interface SafeAnnotationMetadata {
  /**
   * @return the timestamp (a Unix timestamp).
   */
  public long getTimestamp();

  /**
   * @return information about the tool.
   */
  public String getTool();
}
