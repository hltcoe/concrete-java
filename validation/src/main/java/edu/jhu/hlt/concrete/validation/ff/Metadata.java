/**
 *
 */
package edu.jhu.hlt.concrete.validation.ff;

import edu.jhu.hlt.concrete.AnnotationMetadata;

/**
 *
 */
public class Metadata {

  /**
   *
   */
  private Metadata() {

  }

  public static final FlattenedMetadata validate(AnnotationMetadata md) throws InvalidConcreteStructException {
    return new FlatMetadataImpl(md);
  }
}
