package edu.jhu.hlt.concrete.validation.ff;

import java.util.UUID;

/**
 * An interface representing a valid {@link UUID} object.
 */
public interface ValidUUID {
  /**
   * @return the underlying {@link UUID}
   */
  public UUID get();

  /**
   * @return the Concrete representation of this UUID
   */
  public edu.jhu.hlt.concrete.UUID toConcrete();
}
