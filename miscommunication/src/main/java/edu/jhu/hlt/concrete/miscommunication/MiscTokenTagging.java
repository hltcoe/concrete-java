/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public interface MiscTokenTagging extends CommunicationAnnotationWithCommunicationID {

  public static class Builder extends MiscTokenTagging_Builder {
    public Builder() {

    }
  }
}
