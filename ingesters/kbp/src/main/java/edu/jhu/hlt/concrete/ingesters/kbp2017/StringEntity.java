package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = StringEntity.Builder.class)
public abstract class StringEntity {
  public abstract String getID();
  public abstract List<Mention> getMentions();

  static class Builder extends StringEntity_Builder {
    Builder() {

    }
  }
}
