/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.ingesters.base;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.metadata.AnnotationMetadatable;

/**
 * Interface that supports ingesters of Concrete documents. Ingesters
 * perform the task of taking some sort of input and mapping that
 * to a {@link Communication} object.
 */
public interface Ingester extends AnnotationMetadatable {
  /**
   * @return the kind of {@link Communication} objects this {@link Ingester} produces.
   */
  public String getKind();
}
