/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public interface MiscEntityMentionGroup extends CommunicationAnnotationWithCommunicationID {

  public static class Builder extends MiscEntityMentionGroup_Builder {
    public Builder() {

    }
  }
}
