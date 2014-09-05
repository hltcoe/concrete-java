/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

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
   * Generic method to serialize a thrift-like object.
   * 
   * @param object - a 'thrift-like' {@link TBase}] object that can be used by
   * {@link TSerializer#serialize(TBase)} to produce a byte array.
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
   * Generic method to deserialize a thrift-like object.
   * 
   * @param object - a 'thrift-like' [{@link TBase}] object that will be deserialized into. In other words,
   * if you were reading in a {@link Communication} byte array, you should pass in a <code>new Communication()</code>
   * object as the first parameter. 
   * @param bytez - the byte array that holds the serialized {@link TBase} object.
   * @return a deserialized {@link TBase} object. 
   * @throws ConcreteException if there is an error during deserialization.
   */
  public <T extends TBase<T, ? extends TFieldIdEnum>> T fromBytes(T object, byte[] bytez) throws ConcreteException {
    try {
      new TDeserializer(this.strategy).deserialize(object, bytez);
      return object;
    } catch (TException e) {
      throw new ConcreteException("Error during deserialization.", e);
    }
  }
  
  /**
   * Same as {@link #fromBytes(TBase, byte[])}, but takes in a {@link Path} object.
   * 
   * @see #fromBytes(TBase, byte[])
   */
  public <T extends TBase<T, ? extends TFieldIdEnum>> T fromPath(T object, Path pathToSerializedFile) throws ConcreteException {
    try {
      return this.fromBytes(object, Files.readAllBytes(pathToSerializedFile));
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }
  
  /**
   * Same as {@link #fromBytes(TBase, byte[])}, but takes in a {@link String} that represents
   * a path to a serialized {@link TBase} object on disk. 
   * 
   * @see #fromBytes(TBase, Path)
   */
  public <T extends TBase<T, ? extends TFieldIdEnum>> T fromFileString(T object, String pathToSerializedFileString) throws ConcreteException {
    return this.fromBytes(object, Paths.get(pathToSerializedFileString));
  }
  
  /**
   * Same as {@link #fromBytes(TBase, byte[])}, but takes in a {@link InputStream} that represents
   * a serialized {@link TBase} object.
   * 
   * @see #fromBytes(TBase, Path)
   */
  public <T extends TBase<T, ? extends TFieldIdEnum>> T fromInputStream(T object, InputStream is) throws ConcreteException {
    try {
      return this.fromBytes(object, IOUtils.toByteArray(is));
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }
}
