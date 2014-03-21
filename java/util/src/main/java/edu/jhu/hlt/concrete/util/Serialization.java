/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import edu.jhu.hlt.concrete.Communication;

/**
 * Use a default strategy, {@link TBinaryProtocol}, to de/serialize {@link Communication} objects.
 * 
 * @author max
 */
public class Serialization {

  /**
   * 
   */
  private Serialization() {

  }

  public static byte[] toBytes(Communication c) throws TException {
    return new TSerializer().serialize(c);
  }
  
  public static Communication fromBytes(byte[] bytez) throws TException {
    TDeserializer deser = new TDeserializer(new TBinaryProtocol.Factory());
    Communication c = new Communication();
    deser.deserialize(c, bytez);
    return c;
  }
}
