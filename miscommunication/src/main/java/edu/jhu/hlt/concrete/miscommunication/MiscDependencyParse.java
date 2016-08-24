/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public interface MiscDependencyParse extends CommunicationAnnotationWithCommunicationID {

  public static class Builder extends MiscDependencyParse_Builder {
    public Builder() {

    }
  }
}
