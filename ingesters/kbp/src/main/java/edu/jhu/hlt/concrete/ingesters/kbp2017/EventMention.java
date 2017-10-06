package edu.jhu.hlt.concrete.ingesters.kbp2017;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = EventMention.Builder.class)
abstract public class EventMention {
  public abstract Mention getMention();
  public abstract Realis getRealis();

  public static class Builder extends EventMention_Builder {
    public Builder() {

    }
  }
}
