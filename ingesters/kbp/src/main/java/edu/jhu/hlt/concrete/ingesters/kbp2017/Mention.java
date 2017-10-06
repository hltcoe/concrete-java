package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Mention.Builder.class)
public abstract class Mention {

  public abstract MentionType getType();
  public abstract String getText();
  public abstract Provenance getProvenance();
  public abstract UUID getUUID();

  public boolean withinDocument (String id) {
    return this.getProvenance().withinDocument(id);
  }

  static class Builder extends Mention_Builder {
    Builder() {

    }
  }
}
