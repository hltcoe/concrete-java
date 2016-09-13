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
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Interface whose implementers should be able to serialize {@link Communication} objects to <code>.tar.gz</code> files.
 */
public interface CommunicationTarGzSerializer extends CommunicationTarSerializer {

  /**
   * @param commColl
   *          a {@link Collection} of {@link Communication} objects to serialize to a <code>.tar.gz</code> file
   * @param outPath
   *          the {@link Path} to write the .tar.gz file to
   * @throws ConcreteException
   *           on serialization error (e.g., if a {@link Communication} is missing required fields)
   * @throws IOException
   *           on I/O error
   */
  public void toTarGz(Collection<Communication> commColl, Path outPath) throws ConcreteException, IOException;

  /**
   * Default implementation. Calls {@link #toTarGz(Collection, Path)}.
   *
   * @see #toTarGz(Collection, Path)
   * @param commColl
   *          a {@link Collection} of {@link Communication} objects to serialize to a <code>.tar.gz</code> file
   * @param outPathString
   *          the {@link String} representing a path to write the .tar.gz file to
   * @throws ConcreteException
   *           on serialization error (e.g., if a {@link Communication} is missing required fields)
   * @throws IOException
   *           on I/O error
   */
  default void toTarGz(Collection<Communication> commColl, String outPathString) throws ConcreteException, IOException {
    this.toTarGz(commColl, Paths.get(outPathString));
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
  public Iterator<Communication> fromTarGz(InputStream is) throws ConcreteException, IOException;
}
