/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.data;

import java.io.InputStream;
import java.util.Iterator;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Tiny interface to standardize more general streaming Concrete conversion tools.
 * 
 * @author max
 *
 */
public interface StreamingCommunicationConverter {
  /**
   * @param is
   *          an {@link InputStream} to stream over
   * @return an {@link Iterator} over a stream that produces {@link Communication} objects.
   * @throws ConcreteException
   *           if there is an error processing the stream.
   */
  public Iterator<Communication> iterator(InputStream is) throws ConcreteException;
}
