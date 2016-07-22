/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization.iterators;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.acute.iterators.tar.TarGzArchiveEntryByteIterator;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.utilt.AutoCloseableIterator;

/**
 * Allows consumption of a <code>.tar.gz</code> file of Concrete
 * {@link Communication}s.
 */
public class TarGzArchiveEntryCommunicationIterator implements AutoCloseableIterator<Communication> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TarGzArchiveEntryCommunicationIterator.class);

  private final InputStream in;
  private final TarGzArchiveEntryByteIterator byteIter;

  protected static final CommunicationSerializer cs = new CompactCommunicationSerializer();

  public static final TarGzArchiveEntryCommunicationIterator fromPath(Path p) throws IOException {
    return new TarGzArchiveEntryCommunicationIterator(
        new BufferedInputStream(Files.newInputStream(p)));
  }

  /**
   * @throws IOException
   *
   */
  public TarGzArchiveEntryCommunicationIterator(InputStream is) throws IOException {
    this.in = is;
    this.byteIter = new TarGzArchiveEntryByteIterator(is);
  }

  @Override
  public boolean hasNext() {
    return this.byteIter.hasNext();
  }

  @Override
  public Communication next() {
    try {
      return cs.fromBytes(this.byteIter.next());
    } catch (ConcreteException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("You can't remove with this iterator.");
  }

  @Override
  public void close() {
    try {
      in.close();
    } catch (IOException e) {
      LOGGER.warn("Caught exception whilst closing the stream: {}", e.getMessage());
    }
  }
}
