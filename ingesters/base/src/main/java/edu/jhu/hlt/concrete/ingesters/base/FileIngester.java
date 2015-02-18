/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.ingesters.base;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import edu.jhu.hlt.concrete.Communication;

/**
 * An interface for Concrete ingesters that process documents in character-based files
 * (for example, .txt files with UTF-8 characters).
 */
public interface FileIngester {
  /**
   * Convert an entire character-based file to a Concrete {@link Communication}.
   * @param path the {@link Path} to the character-based file.
   * @param charset the {@link Charset} to use. For UTF-8, use {@link StandardCharsets#UTF_8}.
   * @return the prepared {@link Communication}
   * @throws IngestException if there are errors with the conversion (for example,
   * the path points to a binary file that cannot be mapped to characters, or the wrong
   * encoding is specified)
   */
  public Communication fromCharacterBasedFile(Path path, Charset charset) throws IngestException;
}
