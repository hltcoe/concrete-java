/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import java.io.InputStream;
import java.nio.file.Path;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class ThreadSafeCompactCommunicationSerializer extends ThreadSafeThriftSerializer<Communication> implements CommunicationSerializer {

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.CommunicationSerializer#fromBytes(byte[])
   */
  @Override
  public Communication fromBytes(byte[] bytes) throws ConcreteException {
    return super.fromBytes(new Communication(), bytes);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.CommunicationSerializer#toBytes(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public byte[] toBytes(Communication c) throws ConcreteException {
    return super.toBytes(c);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.CommunicationSerializer#fromPath(java.nio.file.Path)
   */
  @Override
  public Communication fromPath(Path path) throws ConcreteException {
    return super.fromPath(new Communication(), path);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.CommunicationSerializer#fromPathString(java.lang.String)
   */
  @Override
  public Communication fromPathString(String pathString) throws ConcreteException {
    return super.fromPathString(new Communication(), pathString);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.serialization.CommunicationSerializer#fromInputStream(java.io.InputStream)
   */
  @Override
  public Communication fromInputStream(InputStream is) throws ConcreteException {
    return super.fromInputStream(new Communication(), is);
  }
}
