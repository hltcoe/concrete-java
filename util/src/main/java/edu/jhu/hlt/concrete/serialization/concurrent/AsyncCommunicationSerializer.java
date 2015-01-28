/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization.concurrent;

import java.util.concurrent.Future;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public interface AsyncCommunicationSerializer extends AutoCloseable {
  public Future<byte[]> toBytes(Communication c) throws ConcreteException;
  public Future<Communication> fromBytes(byte[] bytes) throws ConcreteException;
}
