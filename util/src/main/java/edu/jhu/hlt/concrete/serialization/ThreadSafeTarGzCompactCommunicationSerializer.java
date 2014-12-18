/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class ThreadSafeTarGzCompactCommunicationSerializer extends ThreadSafeCompactCommunicationSerializer implements CommunicationTarGzSerializer {

  @Override
  public Iterator<Communication> fromTarGz(InputStream is) throws ConcreteException, IOException {
    return new TarGzArchiveEntryCommunicationIterator(is);
  }

  @Override
  public void toTarGz(Collection<Communication> commColl, Path outPath) throws ConcreteException {
    try(OutputStream os = Files.newOutputStream(outPath);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(bos);
        TarArchiveOutputStream tos = new TarArchiveOutputStream(gzos);) {
      for (Communication c : commColl) {
        TarArchiveEntry entry = new TarArchiveEntry(c.getId() + ".concrete");
        byte[] cbytes = this.toBytes(c);
        entry.setSize(cbytes.length);
        tos.putArchiveEntry(entry);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(cbytes)) {
          IOUtils.copy(bis, tos);
          tos.closeArchiveEntry();
        }
      }
      
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }

  @Override
  public void toTarGz(Collection<Communication> commColl, String outPathString) throws ConcreteException {
    this.toTarGz(commColl, Paths.get(outPathString));
  }
}
