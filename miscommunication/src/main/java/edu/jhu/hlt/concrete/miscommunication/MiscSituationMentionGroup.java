/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public interface MiscSituationMentionGroup extends CommunicationAnnotationWithCommunicationID {

  public static class Builder extends MiscSituationMentionGroup_Builder {
    public Builder() {

    }
  }
}
