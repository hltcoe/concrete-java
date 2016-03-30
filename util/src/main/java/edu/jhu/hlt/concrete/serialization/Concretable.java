package edu.jhu.hlt.concrete.serialization;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;

public interface Concretable<T extends TBase<T, ? extends TFieldIdEnum>> {
  public T toThrift();
}
