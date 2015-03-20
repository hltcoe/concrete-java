/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.base.stream;

import java.io.InputStream;
import java.util.Iterator;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.Ingester;

/**
 * Interface for Concrete ingesters that process documents from an {@link InputStream}.
 */
public interface IteratorStreamIngester extends Ingester {
  /**
   * @param in the {@link InputStream} from which to consume Concrete {@link Communication} objects.
   * @return an {@link Iterator} of Communication objects
   * @throws IngestException if there is an exception during the conversion (for example,
   * access to the stream is halted)
   */
  public Iterator<Communication> fromInputStream(InputStream in) throws IngestException;
}
