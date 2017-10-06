package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.List;
import java.util.Set;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableSet;

@FreeBuilder
@JsonDeserialize(builder = Event.Builder.class)
abstract public class Event {

  public abstract String getID();
  public abstract String getType();
  public abstract List<EventMention> getMentions();
  public abstract List<EventPredicate> getPredicates();

  @JsonIgnore
  public Set<String> getDocumentIDs() {
    ImmutableSet.Builder<String> b = ImmutableSet.builder();
    this.getMentions().stream()
      .map(EventMention::getMention)
      .map(Mention::getProvenance)
      .map(Provenance::getDocumentID)
      .forEach(b::add);
    this.getPredicates().stream()
      .map(EventPredicate::getPredicate)
      .map(EntityEventPredicate::getRelation)
      .flatMap(r -> r.getProvenances().stream())
      .map(Provenance::getDocumentID)
      .forEach(b::add);
    return b.build();
  }

  public static class Builder extends Event_Builder {
    public Builder() {

    }
  }
}
