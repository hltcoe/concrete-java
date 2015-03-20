/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.base.stream;

import java.io.InputStream;
import java.util.stream.Stream;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.Ingester;

/**
 * Interface for Concrete ingesters that process documents from an {@link InputStream}.
 */
public interface StreamBasedStreamIngester extends Ingester {
  /**
   * @return a {@link Stream} of communication objects
   * @throws IngestException if there is an exception during the conversion (for example,
   * access to the stream is halted)
   */
  public Stream<Communication> stream() throws IngestException;
}
