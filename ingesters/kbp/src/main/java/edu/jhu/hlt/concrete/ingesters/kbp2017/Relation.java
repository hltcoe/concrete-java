package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Relation.Builder.class)
public abstract class Relation {
  public abstract String getEvent();
  public abstract String getTarget();
  public abstract double getConfidence();
  public abstract List<Provenance> getProvenances();

  static class Builder extends Relation_Builder {
    Builder() {

    }
  }
}
