package edu.jhu.hlt.concrete.validation.ff;

import java.util.HashSet;
import java.util.Set;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;

import edu.jhu.hlt.concrete.serialization.Concretable;

public abstract class AbstractConcreteStructWithNecessarilyUniqueUUIDs<T extends TBase<T, ? extends TFieldIdEnum>>
    implements Concretable<T> {

  protected final Set<ValidUUID> necessarilyUniqueUUIDs;
  protected final T obj;

  protected AbstractConcreteStructWithNecessarilyUniqueUUIDs(T t) throws InvalidConcreteStructException {
    this.obj = t;
    this.necessarilyUniqueUUIDs = new HashSet<>();
  }

  protected final void addNecessarilyUniqueUUID(edu.jhu.hlt.concrete.UUID concUuid) throws InvalidConcreteStructException {
    ValidUUID vu = UUIDs.validate(concUuid);
    this.addNecessarilyUniqueUUID(vu);
  }

  protected final void addNecessarilyUniqueUUID(ValidUUID uuid) throws InvalidConcreteStructException {
    if (!this.necessarilyUniqueUUIDs.add(uuid))
      throw new InvalidConcreteStructException("Communication contains at least one duplicate UUID: " + uuid.toString());
  }

  @Override
  public final T toThrift() {
    return this.obj;
  }
}
