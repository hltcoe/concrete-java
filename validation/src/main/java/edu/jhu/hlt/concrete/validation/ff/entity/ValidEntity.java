package edu.jhu.hlt.concrete.validation.ff.entity;

import java.util.Optional;
import java.util.Set;

import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.validation.ff.UUIDable;
import edu.jhu.hlt.concrete.validation.ff.ValidUUID;

/**
 * Interface supporting validation of Concrete {@link Entity} objects.
 * <br><br>
 * This interface assumes that the collection of mention UUIDs
 * is actually a {@link Set}. Not only is the order of these mentions
 * not defined, but the operations of the Set collection are more useful
 * (for example, finding shared mentions across multiple Entities).
 */
public interface ValidEntity extends UUIDable {
  public Set<ValidUUID> getMentionIDSet();

  public Optional<String> getType();

  public Optional<String> getCanonicalName();

  public Optional<Double> getConfidence();
}
