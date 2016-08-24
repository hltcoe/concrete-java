/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.Section;

/**
 *
 */
@FreeBuilder
public interface MiscSection extends CommunicationAnnotationWithCommunicationID {

  public abstract String getKind();
  public abstract Map<UUID, MiscSentence> getIdToSentenceMap();

  public abstract Optional<MiscTextSpan> getTextSpan();

  public static MiscSection create(Section s, String commId, String commText) {
    final Builder b = new Builder()
        .setCommunicationID(NonEmptyString.create(commId))
        .setUUID(UUID.fromString(s.getUuid().getUuidString()))
        .setKind(s.getKind());

    Optional.ofNullable(s.getTextSpan())
      .ifPresent(ts -> {
        b.setTextSpan(MiscTextSpan.create(ts, NonEmptyString.create(commText)));
      });
    Optional.ofNullable(s.getSentenceList())
      .map(sl -> sl.stream()
              .map(sent -> MiscSentence.create(sent, commId, commText)))
      .ifPresent(stream -> b.putAllIdToSentenceMap(stream.collect(Collectors.toMap(MiscSentence::getUUID, u -> u))));

    return b.build();
  }

  public static class Builder extends MiscSection_Builder {
    public Builder() {

    }
  }
}
