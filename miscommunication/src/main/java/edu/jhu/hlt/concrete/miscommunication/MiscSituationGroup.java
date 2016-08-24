/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public interface MiscSituationGroup extends CommunicationAnnotationWithCommunicationID {

  public static class Builder extends MiscSituationGroup_Builder {
    public Builder() {

    }
  }
}
