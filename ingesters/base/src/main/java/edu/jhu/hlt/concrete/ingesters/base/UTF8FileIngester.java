/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.ingesters.base;

import java.nio.file.Path;

import edu.jhu.hlt.concrete.Communication;

/**
 * An interface for Concrete ingesters that process documents in character-based files
 * (for example, .txt files with UTF-8 characters).
 */
public interface UTF8FileIngester extends Ingester {
  /**
   * Convert an entire character-based file to a Concrete {@link Communication}. UTF-8
   * encoding is assumed.
   *
   * @param path the {@link Path} to the character-based file.
   * @return the prepared {@link Communication}
   * @throws IngestException if there are errors with the conversion (for example,
   * the path points to a binary file that cannot be mapped to characters, or the encoding
   * is not UTF-8)
   */
  public Communication fromCharacterBasedFile(Path path) throws IngestException;
}
