package edu.jhu.hlt.concrete.validation.ff.structure;

import java.util.Optional;

import edu.jhu.hlt.concrete.SpanLink;
import edu.jhu.hlt.concrete.validation.ff.FlattenedTokenRefSequence;
import edu.jhu.hlt.concrete.validation.ff.ValidUUID;

public interface ValidSpanLink extends FlattenedTokenRefSequence {
  public Optional<String> getExternalTarget();

  public Optional<ValidUUID> getConcreteTarget();

  public String getLinkType();

  public SpanLink toConcrete();
}
