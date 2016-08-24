/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationMetadata;
import edu.jhu.hlt.concrete.TweetInfo;
import edu.jhu.hlt.concrete.TwitterUser;

/**
 *
 *
 */
@FreeBuilder
public interface MiscCommunication {
  public abstract Map<UUID, MiscSection> getSections();
  public abstract NonEmptyString getText();
  public abstract NonEmptyString getId();
  public abstract UUID getUUID();

  public abstract Optional<Long> getAuthorTwitterID();

  public static MiscCommunication create(Communication c) {
    final Builder b = new Builder();
    Optional.ofNullable(c.getCommunicationMetadata())
        .map(CommunicationMetadata::getTweetInfo)
        .map(TweetInfo::getUser)
        .map(TwitterUser::getId)
        .ifPresent(b::setAuthorTwitterID);

    Optional.ofNullable(c.getSectionList())
        .ifPresent(sl -> {
          sl.stream()
            .map(s -> MiscSection.create(s, c.getId(), c.getText()))
            .forEach(sect -> b.putSections(sect.getUUID(), sect));
        });

    return b.setUUID(UUID.fromString(c.getUuid().getUuidString()))
        .setId(NonEmptyString.create(c.getId()))
        .setText(NonEmptyString.create(c.getText()))
        .build();
  }

  public static class Builder extends MiscCommunication_Builder {
    public Builder() {

    }
  }
}
