/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization.archiver;

import edu.jhu.hlt.acute.archivers.Archivable;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Wrapper around {@link Communication} that implements {@link Archivable}.
 */
public class ArchivableCommunication implements Archivable {

  private final Communication comm;
  // Cache id and serializer.
  private final String id;
  private static final CompactCommunicationSerializer cs = new CompactCommunicationSerializer();

  private static final String ext = ".comm";

  /**
   * Wrap a {@link Communication} object.
   */
  public ArchivableCommunication(Communication comm) {
    this.comm = comm;
    this.id = comm.getId();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.acute.archivers.Archivable#getFileName()
   */
  @Override
  public String getFileName() {
    return this.id + ext;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.acute.archivers.Archivable#getBytes()
   */
  @Override
  public byte[] getBytes() {
    try {
      return cs.toBytes(this.comm);
    } catch (ConcreteException e) {
      throw new RuntimeException(e);
    }
  }
}
