/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.tokenized;

import java.util.Optional;

import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;

/**
 * Wrapper around {@link Tokenization}, providing utilities
 * for accessing {@link TokenTagging} objects and other nested
 * fields.
 */
public class PowerTokenization {

  private final Tokenization wrapped;
  private final boolean hasTokenTaggings;

  public PowerTokenization(final Tokenization wrapped) {
    this.wrapped = wrapped;
    this.hasTokenTaggings = this.wrapped.isSetTokenTaggingList();
  }

  private final Optional<TokenTagging> getFirstTT(String kind) {
    return this.wrapped.getTokenTaggingList().stream()
        .sequential()
        .filter(tt -> tt.getTaggingType().equalsIgnoreCase(kind))
        .findFirst();
  }

  /**
   * @return the first {@link TokenTagging} in this {@link Tokenization}
   * with type NER, or {@link Optional#empty()} if no such tagging exists.
   */
  public Optional<TokenTagging> firstNERTagging() {
    return hasTokenTaggings ? this.getFirstTT("NER") : Optional.empty();
  }

  /**
   * @return the first {@link TokenTagging} in this {@link Tokenization}
   * with type LEMMA, or {@link Optional#empty()} if no such tagging exists.
   */
  public Optional<TokenTagging> firstLemmaTagging() {
    return hasTokenTaggings ? this.getFirstTT("LEMMA") : Optional.empty();
  }

  /**
   * @return the first {@link TokenTagging} in this {@link Tokenization}
   * with type POS, or {@link Optional#empty()} if no such tagging exists.
   */
  public Optional<TokenTagging> firstPOSTagging() {
    return hasTokenTaggings ? this.getFirstTT("POS") : Optional.empty();
  }
}
