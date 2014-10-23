/**
 * 
 */
package concrete.util.concurrent;

import java.util.concurrent.Callable;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.CommunicationSerialization;

/**
 * @author max
 *
 */
public class CallableBytesToCommunication implements Callable<Communication> {

  private final CommunicationSerialization cs = new CommunicationSerialization();  
  private final byte[] bytes;
  
  public CallableBytesToCommunication (byte[] bytes) {
    this.bytes = bytes;
  }
  
  @Override
  public Communication call() throws Exception {
    return this.cs.fromBytes(this.bytes);
  }

}
