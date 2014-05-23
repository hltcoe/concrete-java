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
 * @author max
 *
 */
public class CallablePathToCommunication implements Callable<Communication> {

  private final Path p;
  
  /**
   * 
   */
  public CallablePathToCommunication(Path p) {
    this.p = p;
  }

  /* (non-Javadoc)
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public Communication call() throws Exception {
    byte[] bytes = Files.readAllBytes(this.p);
    return new Serialization().fromBytes(new Communication(), bytes);
  }
}
