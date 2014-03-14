/**
 * 
 */
package edu.jhu.hlt.concrete.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;

/**
 * Wrapper around {@link Communication} to allow advanced functionality.
 * 
 * @author max
 */
public class SuperCommunication {

  protected final Communication comm;
  
  /**
   * 
   */
  public SuperCommunication(Communication comm) {
    this.comm = comm;
  }
  
  /**
   * Take in a {@link Path} to an output file, and whether or not to delete
   * the file at that path if it already exists, and output a byte array
   * that represents a serialized {@link Communication} object. 
   * 
   * @param path - a {@link Path} to the destination of the serialized {@link Communication}.
   * @param deleteExisting - whether to delete the file at path, if it exists.
   * @throws ConcreteException if there are {@link IOException}s or {@link TException}s.
   */
  public void writeToFile(Path path, boolean deleteExisting) throws ConcreteException {
    try {
      if (deleteExisting)
        Files.deleteIfExists(path);
      else
        if (Files.exists(path))
          throw new ConcreteException("File exists at: " + path.toString() + ". Delete it, or "
              + "call this method with the second parameter set to 'true'.");
    
      byte[] bytez = Serialization.toBytes(this.comm);
      Files.write(path, bytez);
    } catch (TException e) {
      throw new ConcreteException(e);
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }
}
