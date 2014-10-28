/**
 * 
 */
package concrete.util.concurrent;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.communications.SuperCommunication;
import edu.jhu.hlt.concrete.util.CommunicationSerialization;

/**
 * @author max
 *
 */
public class CallableBytesToConcreteFile implements Callable<Void> {

  private final CommunicationSerialization cs = new CommunicationSerialization();  
  private final byte[] bytes;
  private final Path outPath;
  
  public CallableBytesToConcreteFile (byte[] bytes, Path outPath) {
    this.bytes = bytes;
    this.outPath = outPath;
  }
  
  /* (non-Javadoc)
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public Void call() throws Exception {
    Communication c = cs.fromBytes(this.bytes);
    new SuperCommunication(c).writeToFile(this.outPath, true);
    return null;
  }
}
