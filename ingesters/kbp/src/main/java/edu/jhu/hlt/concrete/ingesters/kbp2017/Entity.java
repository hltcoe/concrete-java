package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import edu.jhu.hlt.concrete.uuid.UUIDFactory;

@FreeBuilder
@JsonDeserialize(builder = Entity.Builder.class)
public abstract class Entity {

  public abstract String getID();
  public abstract EntityType getType();
  public abstract UUID getUUID();
  public abstract List<Mention> getMentions();
  public abstract List<Relation> getRelations();
  public abstract List<EntityEventPredicate> getEvents();

  @JsonIgnore
  public Set<String> getDocumentIDs() {
    ImmutableSet.Builder<String> b = ImmutableSet.builder();
    this.getMentions().stream()
      .map(Mention::getProvenance)
      .map(Provenance::getDocumentID)
      .forEach(b::add);
    this.getRelations().stream()
      .flatMap(r -> r.getProvenances().stream())
      .map(Provenance::getDocumentID)
      .forEach(b::add);
    this.getEvents().stream()
      .map(EntityEventPredicate::getRelation)
      .flatMap(r -> r.getProvenances().stream())
      .map(Provenance::getDocumentID)
      .forEach(b::add);
    return b.build();
  }

  @JsonIgnore
  public List<String> getCanonicalMentionStrings() {
    return ImmutableList.copyOf(this.getMentions().stream()
      .filter(m -> m.getType() == MentionType.CANONICAL_MENTION)
      .map(Mention::getText)
      .collect(Collectors.toList()));
  }

  @JsonIgnore
  public Optional<String> getMostCommonCanonicalMention() {
    List<String> cmsl = this.getCanonicalMentionStrings();
    if (cmsl.isEmpty())
      return Optional.empty();
    Multiset<String> hms = HashMultiset.create();
    hms.addAll(cmsl);
    Entry<String> highest = null;
    for (Entry<String> e : hms.entrySet()) {
      if (highest == null || e.getCount() > highest.getCount())
        highest = e;
    }
    return Optional.ofNullable(highest.getElement());
  }

  @JsonIgnore
  public edu.jhu.hlt.concrete.Entity toConcrete() {
    edu.jhu.hlt.concrete.Entity concE = new edu.jhu.hlt.concrete.Entity()
        .setUuid(UUIDFactory.fromJavaUUID(this.getUUID()))
        .setId(this.getID())
        .setType(this.getType().toString())
        .setConfidence(1.0d);
    this.getMostCommonCanonicalMention().ifPresent(concE::setCanonicalName);
    return concE;
  }

  static class Builder extends Entity_Builder {
    Builder() {

    }
  }
}
