package edu.jhu.hlt.concrete.miscommunication;

import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface MiscToken {

  public int getIndex();
  public MiscTextSpan getTextSpan();
  public MiscTokenization getTokenization();

  public Optional<NonEmptyString> getLemma();

  public static class Builder extends MiscToken_Builder {
    public Builder() {

    }
  }
}
