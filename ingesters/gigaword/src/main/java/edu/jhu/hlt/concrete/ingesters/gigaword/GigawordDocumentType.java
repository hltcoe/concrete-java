/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

/**
 * Enumeration of all Gigaword document types.
 */
public enum GigawordDocumentType {
  ADVIS("advis"),
  MULTI("multi"),
  OTHER("other"),
  STORY("story");

  private final String v;

  private GigawordDocumentType(String v) {
    this.v = v;
  }

  /**
   * Cheat method so that {@link #toString()} can be overriden.
   */
  private String getValue() {
    return this.v;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.v;
  }

  /**
   * Check a string. If it is equal to any enum value (ignoring case sensitivity),
   * return the corresponding enum value.
   *
   * @param v a {@link String} mapping to a {@link GigawordDocumentType} enum value
   * @return a {@link GigawordDocumentType} enum
   * @throws IllegalArgumentException if v is not truly an enum value
   */
  public static final GigawordDocumentType getEnumeration(String v) {
    for (GigawordDocumentType t : GigawordDocumentType.values())
      if (t.getValue().equalsIgnoreCase(v))
        return t;
    throw new IllegalArgumentException("No enum matching value: " + v);
  }
}
