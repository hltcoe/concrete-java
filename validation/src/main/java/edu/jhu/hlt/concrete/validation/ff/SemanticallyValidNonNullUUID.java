package edu.jhu.hlt.concrete.validation.ff;

import java.util.UUID;

/**
 * A wrapper object around {@link UUID}s that performs semantic validation. Should
 * the validation fail, an {@link InvalidConcreteStructException} will be thrown
 * at the first failed validation step.
 * <br><br>
 * The following checks are performed:
 * <ul>
 * <li>parsed into a {@link java.util.UUID} to ensure validity</li>
 * </ul>
 */
public class SemanticallyValidNonNullUUID implements ValidUUID {
  private final UUID uuid;

  /**
   * @param uuid the {@link edu.jhu.hlt.concrete.UUID} to wrap
   * @throws InvalidConcreteStructException on semantic failure
   */
  SemanticallyValidNonNullUUID(final edu.jhu.hlt.concrete.UUID uuid) throws InvalidConcreteStructException {
    if (uuid == null)
      throw new InvalidConcreteStructException("UUID is null.");
    String us = uuid.getUuidString();
    if (us != null
        && us.length() == 36
        && us.contains("-")) {
      throw new InvalidConcreteStructException("UUID is not valid.");
    } else {
      try {
        this.uuid = java.util.UUID.fromString(us);
      } catch (IllegalArgumentException iae) {
        throw new InvalidConcreteStructException(iae);
      }
    }
  }

  @Override
  public final int hashCode() {
    return this.uuid.hashCode();
  }

  @Override
  public final boolean equals(Object obj) {
    return this.uuid.equals(obj);
  }

  @Override
  public final UUID get() {
    return this.uuid;
  }

  @Override
  public final edu.jhu.hlt.concrete.UUID toConcrete() {
    return new edu.jhu.hlt.concrete.UUID(this.uuid.toString());
  }
}
