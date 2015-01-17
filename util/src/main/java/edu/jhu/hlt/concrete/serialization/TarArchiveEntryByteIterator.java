/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.serialization;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

/**
 * @author max
 *
 */
class TarArchiveEntryByteIterator implements Iterator<byte[]> {

  private final TarArchiveInputStream tis;
  
  /**
   * @throws IOException 
   * 
   */
  public TarArchiveEntryByteIterator(InputStream is) throws IOException {
    this.tis = new TarArchiveInputStream(new BufferedInputStream(is));
    
    // Prepare next entry.
    this.tis.getNextTarEntry();
  }

  @Override
  public boolean hasNext() {
    // couple possible states:
    // processed 1 file, and are now on a dir.
    // processed 1 file, and are now on another file.
    // done iterating (nothing left).
    
    // if any entry is null, done; return false.
    while (this.tis.getCurrentEntry() != null) {
      TarArchiveEntry entry = this.tis.getCurrentEntry();
      if (!entry.isDirectory())
        return true;
      else
        try {
          this.tis.getNextEntry();
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }
    }

    return this.tis.getCurrentEntry() != null;
  }

  @Override
  public byte[] next() {
    try {
      TarArchiveEntry entry = this.tis.getCurrentEntry();
      if (entry.isDirectory()) {
        // Recurse.
        this.tis.getNextTarEntry();
        this.next();
      }
      
      byte[] bytes = IOUtils.toByteArray(this.tis);
      this.tis.getNextTarEntry();
      return bytes;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("You can't remove with this iterator.");
  }
}
