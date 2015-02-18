/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.ingesters.base;

import edu.jhu.hlt.concrete.Communication;

/**
 * An interface for Concrete ingesters that process text documents.
 */
public interface TextIngester {
  /**
   * Convert a {@link String} to a {@link Communication} object.
   * @param content {@link String} representing something to map to a {@link Communication}
   * @return the produced {@link Communication}
   * @throws IngestException if there are errors with the conversion
   */
  public Communication fromString(String content) throws IngestException;
}
