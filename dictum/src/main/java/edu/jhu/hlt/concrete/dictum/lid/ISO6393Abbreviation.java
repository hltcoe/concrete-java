package edu.jhu.hlt.concrete.dictum.lid;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public abstract class ISO6393Abbreviation {

  public abstract String getAbbreviation();

  ISO6393Abbreviation() {
  }

  public static ISO6393Abbreviation fromAbbreviation(String abbreviation) throws InvalidISO6393AbbreviationException {
    if (abbreviation.length() != 3
        || !ValidISO3Languages.isValidISO3Abbreviation(abbreviation))
      throw new InvalidISO6393AbbreviationException(abbreviation);
    return new ISO6393Abbreviation.Builder()
        .setAbbreviation(abbreviation)
        .build();
  }

  public static ISO6393Abbreviation fromAbbreviationUnchecked(String abbreviation) {
    if (abbreviation.length() != 3
        || !ValidISO3Languages.isValidISO3Abbreviation(abbreviation))
      throw new IllegalArgumentException(new InvalidISO6393AbbreviationException(abbreviation).getMessage());
    return new ISO6393Abbreviation.Builder()
        .setAbbreviation(abbreviation)
        .build();
  }

  static class Builder extends ISO6393Abbreviation_Builder {
    Builder() { }
  }
}
