/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;

/**
 * @author max
 *
 */
public class ValidatableTokenization extends AbstractAnnotation<Tokenization> {

  /**
   * @param annotation
   */
  public ValidatableTokenization(Tokenization annotation) {
    super(annotation);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    return true;
  }

  /**
   * Check:
   * <ol>
   * <li>UUID is valid</li>
   * <li>Metadata is set</li>
   * <li>Metadata is valid</li>
   * <li>TokenizationKind is set</li>
   * </ol>
   *
   * <ul>
   * <li>If TokenizationKind == Lattice, check Lattice exists and List does not</li>
   * <li>If TokenizationKind == List, check List exists and Lattice does not; validate List[Token]</li>
   * </ul>
   */
  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    boolean basics = this.validateUUID(this.annotation.getUuid())
        && this.printStatus("Metadata must be set", this.annotation.isSetMetadata())
        && this.printStatus("Metadata must be valid", new ValidatableMetadata(this.annotation.getMetadata()).isValid())
        && this.printStatus("TokenizationKind must be set.", this.annotation.isSetKind());
    if (!basics)
      return false;
    else {
      boolean validByType = true;
      if (this.annotation.getKind() == TokenizationKind.TOKEN_LATTICE)
        validByType = this.printStatus("Kind == LATTICE, so lattice must be set, AND list must NOT be set.", this.annotation.isSetLattice() && !this.annotation.isSetTokenList());

      else {
        validByType = this.printStatus("Kind == LIST, so list must be set, AND list must NOT be set.", this.annotation.isSetTokenList() && !this.annotation.isSetLattice())
            && this.printStatus("TokenList must not be empty.", this.annotation.getTokenList().getTokenListSize() > 0)
            && this.printStatus("TokenList must be valid.", this.validateTokenList());
        if (validByType) {
          Iterator<TokenTagging> iter = this.annotation.getTokenTaggingListIterator();
          boolean ttsValid = true;
          while (ttsValid && iter.hasNext()) {
            // Check validity of each TokenTagging.
            TokenTagging tt = iter.next();
            ttsValid = new ValidatableTokenTagging(tt, this.annotation).isValid();
          }
        }
      }

      return validByType;
    }
  }

  /**
   * Validate a {@link List} of {@link Token}s relative
   * to a {@link Tokenization} object.
   *
   * @return <code>true</code> if valid
   */
  private boolean validateTokenList() {
    // Populate a set of integers with the token IDs.
    Set<Integer> tokenIdSet = new HashSet<Integer>();

    // Iterate over tokens to validate them.
    Iterator<Token> iter = this.annotation.getTokenList().getTokenListIterator();
    boolean validTokenIdx = true;
    while (validTokenIdx && iter.hasNext()) {
      Token current = iter.next();
      boolean isSetIdx = this.printStatus("Token idx must be set.", current.isSetTokenIndex());
      if (!isSetIdx)
        return false;
      else {
        int idx = current.getTokenIndex();
        if (!this.printStatus("Token idx can't be < 0", idx >= 0))
          return false;
        else {
          // If set is empty, can safely add.
          if (tokenIdSet.isEmpty())
            tokenIdSet.add(idx);
          else {
            // IF set is not empty, need to make sure it is not already present.
            validTokenIdx = tokenIdSet.add(idx);
          }
        }
      }
    }

    // have now gotten all of the token IDs, or exited when one was invalid.
    // First, make sure that validTokenIdx is still true.
    if (!validTokenIdx)
      return false;
    else {
      // Need to make sure that this set is continuous from 0..K
      return this.printStatus("Token indices must be continuous from 0.." + tokenIdSet.size(), this.validateTokenSet(tokenIdSet));
    }
  }

  private boolean validateTokenSet(Set<Integer> tokenIdxSet) {
    boolean validOrder = true;
    final int sz = tokenIdxSet.size();

    // From 0..K, generate an int.
    for (int i = 0; i < sz; i++) {
      // If previous was invalid, exit with false.
      if (!validOrder)
        return false;
      else
        validOrder = tokenIdxSet.contains(i);
    }

    // Return order validity.
    return validOrder;
  }

}
