/**
 * 
 */
package edu.jhu.hlt.concrete.examples;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.SentenceSegmentationCollection;

/**
 * @author max
 *
 */
public interface SentenceSegmenter {
  public SentenceSegmentationCollection generateSentenceSegmentations(Communication c) throws AnnotationException;
}
