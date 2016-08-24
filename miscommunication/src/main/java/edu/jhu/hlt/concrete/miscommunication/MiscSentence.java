/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import java.util.Optional;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.Sentence;

/**
 *
 */
@FreeBuilder
public interface MiscSentence extends CommunicationAnnotationWithCommunicationID {

  public abstract NonEmptyString getCommunicationID();

  public abstract Optional<MiscTokenization> getTokenization();
  public abstract Optional<MiscTextSpan> getTextSpan();

  public static MiscSentence create(Sentence s, String commId, String commText) {
    final NonEmptyString ctxt = NonEmptyString.create(commText);
    Optional<MiscTextSpan> omts = Optional.ofNullable(s.getTextSpan())
        .map(ts -> MiscTextSpan.create(ts, ctxt));
    return new Builder()
        .setCommunicationID(NonEmptyString.create(commId))
        .setUUID(UUID.fromString(s.getUuid().getUuidString()))
        .setTextSpan(omts)
        .build();
  }

  public static class Builder extends MiscSentence_Builder {
    public Builder() {

    }
  }
}
