/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import edu.jhu.hlt.concrete.Communication;

/**
 * 
 * 
 * @author max
 */
public class Serialization {

  private final TProtocolFactory strategy;
  
  /**
   * Use a default strategy, {@link TBinaryProtocol}, to de/serialize {@link Communication} objects.
   */
  public Serialization() {
    this.strategy = new TBinaryProtocol.Factory();
  }

  /**
   * Serialize a thrift-like object.
   * 
   * @param object
   * @return
   * @throws ConcreteException
   */
  public <T extends TBase<T, ? extends TFieldIdEnum>> byte[] toBytes(T object) throws ConcreteException {
    try {
      return new TSerializer(this.strategy).serialize(object);
    } catch (TException e) {
      throw new ConcreteException("Error during serialization.", e);
    }
  }
  
  /**
   * Deserialize a thrift-like object.
   * 
   * @param object
   * @param bytez
   * @return
   * @throws ConcreteException
   */
  public <T extends TBase<T, ? extends TFieldIdEnum>> T fromBytes(T object, byte[] bytez) throws ConcreteException {
    try {
      new TDeserializer(this.strategy).deserialize(object, bytez);
      return object;
    } catch (TException e) {
      throw new ConcreteException("Error during deserialization.", e);
    }
  }
}
