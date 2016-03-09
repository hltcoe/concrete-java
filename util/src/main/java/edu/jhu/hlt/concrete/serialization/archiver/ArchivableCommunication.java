/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization.archiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.acute.archivers.Archivable;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Wrapper around {@link Communication} that implements {@link Archivable}.
 */
public class ArchivableCommunication implements Archivable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArchivableCommunication.class);

  private final Communication comm;
  // Cache id and serializer.
  private final String id;
  private final CompactCommunicationSerializer cs;

  private static final String ext = ".comm";

  /**
   * Wrap a {@link Communication} object.
   */
  public ArchivableCommunication(Communication comm) {
    this.comm = comm;
    this.id = comm.getId();
    this.cs = new CompactCommunicationSerializer();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.acute.archivers.Archivable#getFileName()
   */
  @Override
  public String getFileName() {
    if (this.id.length() > 95) {
      LOGGER.info("Truncating filename: {}", this.id);
      String trunc = this.id.substring(0, 94);
      String fn = trunc + ext;
      LOGGER.info("New filename: {}", fn);
      return fn;
    } else
      return this.id + ext;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.acute.archivers.Archivable#getBytes()
   */
  @Override
  public byte[] getBytes() {
    try {
      return this.cs.toBytes(this.comm);
    } catch (ConcreteException e) {
      throw new RuntimeException(e);
    }
  }
}
