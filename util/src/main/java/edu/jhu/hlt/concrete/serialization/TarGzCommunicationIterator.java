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
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class TarGzCommunicationIterator implements Iterator<Communication> {

  private final BufferedInputStream bis;
  private final GzipCompressorInputStream gis;
  private final TarArchiveInputStream tis;
  
  protected final CommunicationSerializer cs = new ThreadSafeCompactCommunicationSerializer();
  
  /**
   * @throws IOException 
   * 
   */
  public TarGzCommunicationIterator(InputStream is) throws IOException {
    this.bis = new BufferedInputStream(is);
    this.gis = new GzipCompressorInputStream(bis);
    this.tis = new TarArchiveInputStream(gis);

    // Prepare next entry.
    this.tis.getNextTarEntry();
  }

  @Override
  public boolean hasNext() {
    // couple possible states:
    // processed 1 comm file, and are now on a dir.
    // processed 1 comm file, and are now on another .comm file.
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
  public Communication next() {
    try {
      TarArchiveEntry entry = this.tis.getCurrentEntry();
      if (entry.isDirectory()) {
        // Recurse.
        this.tis.getNextTarEntry();
        this.next();
      }
      
      byte[] bytes = IOUtils.toByteArray(tis);
      this.tis.getNextTarEntry();
      return this.cs.fromBytes(bytes);
    } catch (IOException | ConcreteException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("You can't remove with this iterator.");
  }
}
