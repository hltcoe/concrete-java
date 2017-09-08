package edu.jhu.hlt.concrete.ingesters.kbp2017;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = EntityEventPredicate.Builder.class)
public abstract class EntityEventPredicate {

  public abstract Realis getRealis();
  public abstract Relation getRelation();

  static class Builder extends EntityEventPredicate_Builder {

  }
}
