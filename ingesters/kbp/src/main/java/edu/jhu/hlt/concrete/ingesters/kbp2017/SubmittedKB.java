package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.Map;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = SubmittedKB.Builder.class)
public abstract class SubmittedKB {

  public abstract Map<String, Entity> getEntityMap();
  public abstract Map<String, StringEntity> getStringEntityMap();
  public abstract String getKBName();

  static class Builder extends SubmittedKB_Builder {
    Builder() {

    }
  }
}
