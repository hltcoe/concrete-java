/*
 * 
 */
package concrete.util.concurrent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.Serialization;

/**
 * Quick and dirty implementation of {@link Callable} that can be used to
 * parallelize loading of {@link Communication} objects from serialized files
 * on disk.
 * 
 * @author max
 */
public class CallablePathToCommunication implements Callable<Communication> {

  private final Path p;
  
  /**
   * Single arg ctor: pass in a {@link Path} to a serialized {@link Communication} file on disk.  
   */
  public CallablePathToCommunication(Path p) {
    this.p = p;
  }

  /**
   * Read in the file from disk as a byte array, then deserialize it into a {@link Communication}
   * object.
   */
  /* (non-Javadoc)
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public Communication call() throws Exception {
    byte[] bytes = Files.readAllBytes(this.p);
    return new Serialization().fromBytes(new Communication(), bytes);
  }
}
