/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public interface MiscParse extends CommunicationAnnotationWithCommunicationID {

  public static class Builder extends MiscParse_Builder {
    public Builder() {

    }
  }
}
