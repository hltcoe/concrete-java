/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Utility class for convenient serialization of Thrift-like data structures. 
 * 
 * @author max
 */
public class ThreadSafeThriftSerializer<T extends TBase<T,? extends TFieldIdEnum>> implements ThriftSerializer<T> {

  private final TProtocolFactory strategy;
  
  /**
   * Use a default strategy, {@link TCompactProtocol}, to de/serialize Thrift-like objects.
   */
  public ThreadSafeThriftSerializer() {
    this(new TCompactProtocol.Factory());
  }
  
  /**
   * Support additional {@link TProtocolFactory} impls (e.g. {@link TBinaryProtocol}).
   * @param factory
   */
  public ThreadSafeThriftSerializer(TProtocolFactory factory) {
    this.strategy = factory;
  }

  /**
   * Generic method to serialize a thrift-like object.
   * 
   * @param object - a 'thrift-like' {@link TBase} object that can be used by
   * {@link TSerializer#serialize(TBase)} to produce a byte array.
   * @return a byte[] representing the object
   * @throws ConcreteException
   */
  public byte[] toBytes(T object) throws ConcreteException {
    try {
      return new TSerializer(this.strategy).serialize(object);
    } catch (TException e) {
      throw new ConcreteException("Error during serialization.", e);
    }
  }
  
  /**
   * Generic method to deserialize a thrift-like object.
   * 
   * @param object - a 'thrift-like' [{@link TBase}] object that will be deserialized into. In other words,
   * if you were reading in a {@link Communication} byte array, you should pass in a <code>new Communication()</code>
   * object as the first parameter. 
   * @param bytez - the byte array that holds the serialized {@link TBase} object.
   * @return a deserialized {@link TBase} object. 
   * @throws ConcreteException if there is an error during deserialization.
   */
  public T fromBytes(T object, byte[] bytez) throws ConcreteException {
    try {
      new TDeserializer(this.strategy).deserialize(object, bytez);
      return object;
    } catch (TException e) {
      throw new ConcreteException("Error during deserialization.", e);
    }
  }
  
  /**
   * Same as {@link #fromBytes(TBase, byte[])}, but takes in a {@link Path} object.
   */
  public T fromPath(T object, Path pathToSerializedFile) throws ConcreteException {
    try {
      return this.fromBytes(object, Files.readAllBytes(pathToSerializedFile));
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }
  
  /**
   * Same as {@link #fromBytes(TBase, byte[])}, but takes in a {@link String} that represents
   * a path to a serialized {@link TBase} object on disk. 
   */
  public T fromPathString(T object, String pathToSerializedFileString) throws ConcreteException {
    return this.fromPath(object, Paths.get(pathToSerializedFileString));
  }
  
  /**
   * Same as {@link #fromBytes(TBase, byte[])}, but takes in a {@link InputStream} that represents
   * a serialized {@link TBase} object.
   */
  public T fromInputStream(T object, InputStream is) throws ConcreteException {
    try {
      return this.fromBytes(object, IOUtils.toByteArray(is));
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }
}
