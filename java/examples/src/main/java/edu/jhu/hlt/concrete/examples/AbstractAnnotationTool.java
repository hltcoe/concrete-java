/**
 * 
 */
package edu.jhu.hlt.concrete.examples;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import edu.jhu.hlt.concrete.AnnotationMetadata;

/**
 * @author max
 *
 */
public abstract class AbstractAnnotationTool {

  /**
   * Generate an {@link AnnotationMetadata} object that describes this "tool".
   *
   * @return a {@link AnnotationMetadata} object for this project
   */
  protected static final AnnotationMetadata getMetadata() {
    AnnotationMetadata md = new AnnotationMetadata();
    md.confidence = 1.0d;
    md.timestamp = (int) (System.currentTimeMillis() / 1000);
    md.tool = "concrete-examples";
    return md;
  }
  
  public static TSerializer getSerializer() {
    return new TSerializer(new TBinaryProtocol.Factory());
  }
  
  public static TDeserializer getDeserializer() {
    return new TDeserializer(new TBinaryProtocol.Factory());
  }
}
