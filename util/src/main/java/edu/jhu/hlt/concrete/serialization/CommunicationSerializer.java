/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
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
public interface CommunicationSerializer {
  public Communication fromBytes(byte[] bytes) throws ConcreteException;
  public byte[] toBytes(Communication c) throws ConcreteException;
  public Communication fromPath(Path path) throws ConcreteException;
  public Communication fromPathString(String pathString) throws ConcreteException;
  public Communication fromInputStream(InputStream is) throws ConcreteException;
}
