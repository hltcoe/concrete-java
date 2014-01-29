/**
 * 
 */
package edu.jhu.hlt.concrete.examples;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.SectionSegmentation;


/**
 * Simple interface for {@link SectionSegmentation} tools.
 * 
 * @author max
 */
public interface SectionSegmenter {
  public SectionSegmentation sectionCommunication(Communication c);
}
