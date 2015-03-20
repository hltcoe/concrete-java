/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.base.stream;

import java.nio.file.Path;
import java.util.Iterator;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.Ingester;

/**
 * Interface that allows streaming of {@link Communication} objects over a UTF-8
 * based text file that contains multiple Communications (for example, a concatenated
 * .gz file from the Gigaword corpus).
 */
public interface UTF8FileStreamIngester extends Ingester {
  /**
   * @param path a {@link Path} from which to generate a stream of {@link Communication} objects
   * @return an {@link Iterator} representing the communication objects from the path
   * @throws IngestException if there is an exception during the conversion (for example,
   * the file is made up of UTF-8 characters, the file does not exist, etc.)
   */
  public Iterator<Communication> fromPath(Path path) throws IngestException;
}
