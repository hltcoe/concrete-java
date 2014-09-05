/**
 * 
 */
package edu.jhu.hlt.concrete.util;

import java.io.InputStream;
import java.nio.file.Path;

import edu.jhu.hlt.concrete.Communication;

/**
 * Utility class for de/serialization {@link Communication} thrift objects.
 * 
 * @see Serialization
 * 
 * @author max
 */
public class CommunicationSerialization {

  private final Serialization ser;
  
  /**
   * 
   */
  public CommunicationSerialization() {
    this.ser = new Serialization();
  }
  
  /**
   * Input bytes from a serialized {@link Communication}. Returns a {@link Communication}
   * 
   * @param bytes - some bytes of a {@link Communication} object
   * @return a {@link Communication} object.
   * @throws ConcreteException if any serialization errors occurred. 
   */
  public Communication fromBytes(byte[] bytes) throws ConcreteException {
    return this.ser.fromBytes(new Communication(), bytes);
  }

  /**
   * Input an {@link InputStream} of {@link Communication} bytes. Return a {@link Communication} object.
   * 
   * @param is - an {@link InputStream} of {@link Communication} bytes
   * @return a {@link Communication} object.
   * @throws ConcreteException if any serialization errors occurred. 
   */
  public Communication fromInputStream(InputStream is) throws ConcreteException {
    return this.ser.fromInputStream(new Communication(), is);
  }
  
  /**
   * Input a {@link Path} to some {@link Communication} bytes on disk. Return a {@link Communication} object.
   * 
   * @param p - a {@link Path} to some {@link Communication} bytes on disk.
   * @return a {@link Communication} object.
   * @throws ConcreteException if any serialization errors occurred. 
   */
  public Communication fromPath(Path p) throws ConcreteException {
    return this.ser.fromPath(new Communication(), p);
  }
  
  
  /**
   * Input a {@link String} that represents a path to some {@link Communication} bytes on disk. Return a {@link Communication} object.
   * 
   * @param ps - a {@link String} to some {@link Communication} bytes on disk.
   * @return a {@link Communication} object.
   * @throws ConcreteException if any serialization errors occurred. 
   */
  public Communication fromPathString(String ps) throws ConcreteException {
    return this.ser.fromPathString(new Communication(), ps);
  }
  
  /**
   * Take a {@link Communication} object and produce a byte array.
   * 
   * @param c - a {@link Communication} object
   * @return a byte array of that {@link Communication} object.
   * @throws ConcreteException if any deserialization errors occurred.
   */
  public byte[] toBytes(Communication c) throws ConcreteException {
    return this.ser.toBytes(c);
  }
}
