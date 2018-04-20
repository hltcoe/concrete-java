/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication.comms;

import java.util.UUID;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.miscommunication.NonEmptyString;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * type-safe wrapper around {@link Communication} with only required fields
 * exposed
 */
@FreeBuilder
public abstract class JavaCommunication {
  public abstract UUID getUUID();
  public abstract String getID();
  public abstract String getType();

  public static JavaCommunication convert(Communication comm) {
    UUID uuid = UUID.fromString(comm.getUuid().getUuidString());
    return new Builder()
        .setID(comm.getId())
        .setType(comm.getType())
        .setUUID(uuid)
        .build();
  }

  public static JavaCommunication create(UUID uuid, String id, String type) {
    return new Builder()
        .setID(id)
        .setType(type)
        .setUUID(uuid)
        .build();
  }

  public Communication partial() {
    Communication c = new Communication();
    c.setUuid(UUIDFactory.fromJavaUUID(this.getUUID()));
    c.setId(this.getID());
    c.setType(this.getType());
    return c;
  }

  static class Builder extends JavaCommunication_Builder {
    Builder() {
    }

    @Override
    public Builder setID(String id) {
      NonEmptyString.create(id);
      return super.setID(id);
    }

    @Override
    public Builder setType(String type) {
      NonEmptyString.create(type);
      return super.setType(type);
    }
  }
}
