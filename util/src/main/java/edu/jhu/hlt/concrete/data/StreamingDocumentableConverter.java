/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.data;

import java.io.InputStream;
import java.util.Iterator;

/**
 * Tiny interface to standardize more general streaming Concrete conversion tools.
 * 
 * @author max
 *
 */
public interface StreamingDocumentableConverter {
  /**
   * @param is
   *          an {@link InputStream} to stream over
   * @return an {@link Iterator} over a stream that produces {@link Documentable} objects.
   */
  public Iterator<Documentable> iterator(InputStream is);
}
