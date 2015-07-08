/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Interface whose implementers should be able to serialize {@link Communication} objects to <code>.tar</code> files.
 */
public interface CommunicationTarSerializer extends CommunicationSerializer {
  public void toTar(Collection<Communication> commColl, Path outPath) throws ConcreteException, IOException;

  default void toTar(Collection<Communication> commColl, String outPathString) throws ConcreteException, IOException {
    this.toTar(commColl, Paths.get(outPathString));
  }

  /**
   * @param is
   *          an {@link InputStream}. Should be closed when finished.
   * @return an {@link Iterator} of {@link Communication} objects
   * @throws ConcreteException
   *           on serialization error (e.g., if a {@link Communication} is missing required fields)
   * @throws IOException
   *           on I/O error
   */
  public Iterator<Communication> fromTar(InputStream is) throws ConcreteException, IOException;
}
