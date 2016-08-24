/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public interface MiscEntityGroup extends CommunicationAnnotationWithCommunicationID {

  public static class Builder extends MiscEntityGroup_Builder {
    public Builder() {

    }
  }
}
