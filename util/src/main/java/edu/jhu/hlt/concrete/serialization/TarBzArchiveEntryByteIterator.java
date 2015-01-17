/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

/**
 * @author max
 *
 */
class TarBzArchiveEntryByteIterator extends TarArchiveEntryByteIterator implements Iterator<byte[]> {

  /**
   * @throws IOException 
   */
  public TarBzArchiveEntryByteIterator(InputStream is) throws IOException {
    super(new BZip2CompressorInputStream(is));
  }
}
