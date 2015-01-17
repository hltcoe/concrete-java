/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * @author max
 *
 */
class TarGzArchiveEntryByteIterator extends TarArchiveEntryByteIterator implements Iterator<byte[]> {

  /**
   * @throws IOException 
   */
  public TarGzArchiveEntryByteIterator(InputStream is) throws IOException {
    super(new GzipCompressorInputStream(is));
  }
}
