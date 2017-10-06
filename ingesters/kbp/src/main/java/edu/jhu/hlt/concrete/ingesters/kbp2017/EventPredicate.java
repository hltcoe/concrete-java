package edu.jhu.hlt.concrete.ingesters.kbp2017;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = EventPredicate.Builder.class)
abstract public class EventPredicate {

  public abstract EntityEventPredicate getPredicate();
  public abstract String getAgent();

  static class Builder extends EventPredicate_Builder {
    public Builder() {

    }
  }
}
