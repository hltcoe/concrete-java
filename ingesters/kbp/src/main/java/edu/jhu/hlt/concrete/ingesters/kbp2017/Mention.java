package edu.jhu.hlt.concrete.ingesters.kbp2017;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Mention.Builder.class)
public abstract class Mention {

  public abstract MentionType getType();
  public abstract String getText();
  public abstract Provenance getProvenance();

  static class Builder extends Mention_Builder {
    Builder() {

    }
  }
}
