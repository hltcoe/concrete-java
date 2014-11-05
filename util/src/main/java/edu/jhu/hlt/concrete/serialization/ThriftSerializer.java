/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import java.io.InputStream;
import java.nio.file.Path;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;

import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Interface for Thrift serializers.
 * 
 * @author max
 */
public interface ThriftSerializer<T extends TBase<T, ? extends TFieldIdEnum>> {
  public T fromBytes(T base, byte[] bytes) throws ConcreteException;
  public byte[] toBytes(T base) throws ConcreteException;
  public T fromPath(T base, Path path) throws ConcreteException;
  public T fromPathString(T base, String pathString) throws ConcreteException;
  public T fromInputStream(T base, InputStream is) throws ConcreteException;
}
