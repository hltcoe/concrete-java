/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import java.util.Map;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public interface MiscTokenization extends CommunicationAnnotationWithCommunicationID {

  public abstract Map<Integer, MiscToken> getTokens();
  public abstract MiscSentence getSentence();
  public abstract NonEmptyString getCommunicationID();

  public static class Builder extends MiscTokenization_Builder {
    public Builder() {

    }
  }
}
