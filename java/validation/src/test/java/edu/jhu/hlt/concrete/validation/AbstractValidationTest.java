/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import java.util.UUID;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationType;

/**
 * @author max
 *
 */
public abstract class AbstractValidationTest {

  protected Communication comm;
  
  /**
   * 
   */
  public AbstractValidationTest() {
    this.comm = this.generateValidCommunication();
  }
  
  public Communication generateValidCommunication() {
    Communication c = new Communication();
    c.uuid = UUID.randomUUID().toString();
    c.id = "corpus_foo_1";
    c.text = "This is a sample.";
    c.type = CommunicationType.OTHER;
    
    return c;
  }
  
  public AnnotationMetadata getMetadata() {
    AnnotationMetadata md = new AnnotationMetadata();
    md.confidence = 1F;
    md.setTimestamp(System.currentTimeMillis());
    md.tool = "Validation library";
    
    return md;
  }
}
