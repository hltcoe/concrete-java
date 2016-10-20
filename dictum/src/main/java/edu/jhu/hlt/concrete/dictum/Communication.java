/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.google.common.collect.ImmutableSet;

import edu.jhu.hlt.concrete.dictum.primitives.IntGreaterThanZero;
import edu.jhu.hlt.concrete.dictum.primitives.UnixTimestamp;

/**
 * A powerful alternative to Concrete communication objects,
 * adding utility functionality and validation not otherwise present.
 */
@FreeBuilder
public abstract class Communication implements FlatMetadataWithUUID {

  public abstract String getId();
  public abstract String getType();
  public abstract String getText();

  public abstract Optional<UnixTimestamp> getStartTime();
  public abstract Optional<UnixTimestamp> getEndTime();

  public abstract List<CommunicationTagging> getTags();
  public abstract Map<String, String> getKVs();
  public abstract List<LanguageID> getLanguageIDs();

  public abstract Map<UUID, Section> getIdToSectionMap();

  public abstract Map<UUID, InDocEntityMentionGroup> getIdToEntityMentionsMap();
  public abstract Map<UUID, InDocEntityGroup> getIdToEntitiesMap();

  public abstract Map<UUID, SituationMentionGroup> getIdToSituationMentionsMap();
  public abstract Map<UUID, SituationGroup> getIdToSituationsMap();

  Communication () { }

  public static class Builder extends Communication_Builder {
    public Builder() {
      // defaults: UUID, kbest = 1, ts = current system time.
      super.setUUID(UUID.randomUUID());
      super.setKBest(IntGreaterThanZero.create(1));
      super.setTimestamp(UnixTimestamp.now());
    }

    @Override
    public Builder setText(String text) {
      if (text.trim().isEmpty())
        throw new IllegalArgumentException("Empty or whitespace-only text is not permitted.");
      else
        return super.setText(text);
    }

    private static final void setAddPredicate(UUID i, Set<UUID> ids) {
      if (!ids.add(i))
        throw new IllegalArgumentException("Necessarily unique UUID: " + i.toString()
          + " is duplicated at least once.");
    }

    @Override
    public Communication build() {
      Communication pc = super.build();
      final Set<UUID> us = new HashSet<>();
      // verify that UUIDs that must be unique
      // are unique.
      us.add(pc.getUUID());
      pc.getTags()
          .stream()
          .map(CommunicationTagging::getUUID)
          .forEach(i -> setAddPredicate(i, us));
      pc.getLanguageIDs()
          .stream()
          .map(LanguageID::getUUID)
          .forEach(i -> setAddPredicate(i, us));
      pc.getIdToSectionMap().keySet()
          .stream()
          .forEach(i -> setAddPredicate(i, us));
      // TODO: snake the validation code / move it
//      pc.getIdToSentenceMap().keySet()
//          .stream()
//          .forEach(i -> setAddPredicate(i, us));
//      pc.getIdToDependencyParsesMap().keySet()
//          .stream()
//          .forEach(dp -> setAddPredicate(dp, us));
//      pc.getIdToParsesMap().keySet()
//          .stream()
//          .forEach(i -> setAddPredicate(i, us));
//      pc.getIdToTokenTaggingsMap().keySet()
//          .stream()
//          .forEach(i -> setAddPredicate(i, us));
//      pc.getIdToTokenizationMap().keySet()
//          .stream()
//          .forEach(i -> setAddPredicate(i, us));
      pc.getIdToEntityMentionsMap().values()
          .stream()
          .forEach(emg -> {
            setAddPredicate(emg.getUUID(), us);
            emg.getIdToEntityMentionMap().keySet()
            .forEach(u -> setAddPredicate(u, us));
          });

      // entities: need to also validate that all entity group
      // mentionSetIDs, if set, are valid EntityMentionGroup UUIDs
      final Set<UUID> emsUUIDs = ImmutableSet.copyOf(pc.getIdToEntityMentionsMap().keySet());
      // also need to validate that each mentionIdSet for each entity
      // is a legit entity mention UUID
      final Set<UUID> emUUIDs = ImmutableSet.copyOf(pc.getIdToEntityMentionsMap().values()
          .stream()
          .flatMap(c -> c.getIdToEntityMentionMap().keySet().stream())
          .collect(Collectors.toSet()));

      pc.getIdToEntitiesMap().values()
          .stream()
          .forEach(eg -> {
            UUID eid = eg.getUUID();
            setAddPredicate(eid, us);
            // validate that entity mention group UUID exists
            // if the pointer is present
            eg.getMentionSetUUID().ifPresent(mi -> {
              if (!emsUUIDs.contains(mi))
                throw new IllegalArgumentException("EntityGroup " + eid.toString() + " has a pointer"
                    + " to a group of mentions that does not exist.");

            eg.getIdToEntityMap().values()
                .forEach(e -> {
                  UUID leid = e.getUUID();
                    setAddPredicate(leid, us);
                    // entity mention UUIDs must capture
                    // all entity mention ID pointers
                    if (!emUUIDs.containsAll(e.getMentionUUIDs())) {
                      throw new IllegalArgumentException("Entity contains at least one mention UUID"
                          + " reference that is not included in the set of all entity mention UUIDs.");
                    }
                  });
                });
          });
      pc.getIdToSituationMentionsMap().values()
          .stream()
          .forEach(smg -> {
            setAddPredicate(smg.getUUID(), us);
            smg.getIdToSituationMentionMap().keySet()
                .forEach(u -> setAddPredicate(u, us));
          });
      pc.getIdToSituationsMap().values()
          .stream()
          .forEach(sg -> {
            setAddPredicate(sg.getUUID(), us);
            sg.getIdToSituationMap().keySet()
                .forEach(u -> setAddPredicate(u, us));
          });

      return pc;
    }
  }
}
