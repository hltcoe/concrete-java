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
import java.util.Set;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import concrete.util.data.ConcreteFactory;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class TarCompactCommunicationSerializer extends ThreadSafeCompactCommunicationSerializer 
    implements CommunicationTarSerializer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(TarCompactCommunicationSerializer.class);
  
  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.CommunicationTarSerializer#toTar(java.util.Collection, java.nio.file.Path)
   */
  @Override
  public void toTar(Collection<Communication> commColl, Path outPath) throws ConcreteException, IOException {
    try(OutputStream os = Files.newOutputStream(outPath);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);) {
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

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.CommunicationTarSerializer#toTar(java.util.Collection, java.lang.String)
   */
  @Override
  public void toTar(Collection<Communication> commColl, String outPathString) throws ConcreteException, IOException {
    this.toTar(commColl, Paths.get(outPathString));
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.CommunicationTarSerializer#fromTar(java.io.InputStream)
   */
  @Override
  public Iterator<Communication> fromTar(InputStream is) throws ConcreteException, IOException {
    return new TarArchiveEntryCommunicationIterator(is);
  }
  
  public static void main(String... args) {
    String outPath = args[0];
    ConcreteFactory cf = new ConcreteFactory();
    Set<Communication> cs = cf.randomCommunicationSet(100);
    Path out = Paths.get(outPath);
    CommunicationTarSerializer ser = new TarCompactCommunicationSerializer();
    try {
      ser.toTar(cs, out);
    } catch (ConcreteException | IOException e) {
      LOGGER.error("Caught exception while exporting comms.", e);
    }
  }
}
