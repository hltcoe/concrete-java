package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Entity.Builder.class)
public abstract class Entity {

  public abstract String getID();
  public abstract EntityType getType();
  public abstract List<Mention> getMentions();
  public abstract List<Relation> getRelations();
  public abstract List<EntityEventPredicate> getEvents();

  static class Builder extends Entity_Builder {
    Builder() {

    }
  }
}
