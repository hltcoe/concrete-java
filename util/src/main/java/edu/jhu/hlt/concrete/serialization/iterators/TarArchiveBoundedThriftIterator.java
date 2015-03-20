/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.serialization.iterators;

import java.io.IOException;
import java.io.InputStream;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;

import edu.jhu.hlt.acute.iterators.tar.TarArchiveEntryByteIterator;
import edu.jhu.hlt.concrete.serialization.BoundedThriftSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.utilt.AutoCloseableIterator;

/**
 *
 */
public class TarArchiveBoundedThriftIterator<T extends TBase<T, ? extends TFieldIdEnum>> implements AutoCloseableIterator<T> {

  private final BoundedThriftSerializer<T> ser;
  private final TarArchiveEntryByteIterator byteIter;

  /**
   * @throws IOException
   * @throws ConcreteException
   */
  public TarArchiveBoundedThriftIterator(Class<T> clazz, InputStream is) throws IOException, ConcreteException {
    this.byteIter = new TarArchiveEntryByteIterator(is);
    this.ser = new BoundedThriftSerializer<>(clazz);
  }

  @Override
  public void close() throws IOException {
    this.byteIter.close();
  }

  @Override
  public boolean hasNext() {
    return this.byteIter.hasNext();
  }

  @Override
  public T next() {
    // Throw earlier for clarity.
    byte[] n = this.byteIter.next();
    try {
      // Below can throw if T is not accurately mapped to the inputStream.
      // e.g., if it is an archive of Sections, and T is Sentences.
      return this.ser.fromBytes(n);
    } catch (ConcreteException e) {
      throw new IllegalArgumentException("Exception occurred during conversion to deserialized"
          + " form. Ensure the InputStream represents the same type as T.", e);
    }
  }

  @Override
  public void remove() {
    // throws UnsupportedOperationException
    this.byteIter.remove();
  }
}
