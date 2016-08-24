/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

/**
 *
 */
@FreeBuilder
public abstract class NonEmptyString {

  public abstract String getContent();

  /**
   *
   */
  NonEmptyString() {
  }

  public static NonEmptyString create(String input) {
    return new Builder()
        .setContent(input)
        .build();
  }

  static class Builder extends NonEmptyString_Builder {
    Builder() {

    }

    @Override
    public Builder setContent(String content) {
      if (content.isEmpty())
        throw new IllegalArgumentException("String cannot be empty.");
      return super.setContent(content);
    }
  }
}
