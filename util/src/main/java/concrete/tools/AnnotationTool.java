/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package concrete.tools;

import edu.jhu.hlt.concrete.AnnotationMetadata;

/**
 * @deprecated
 */
@Deprecated
public interface AnnotationTool {
  /**
   * Clients should implement a method that returns an appropriate
   * {@link AnnotationMetadata} object.
   */
  public AnnotationMetadata getMetadata();
}
