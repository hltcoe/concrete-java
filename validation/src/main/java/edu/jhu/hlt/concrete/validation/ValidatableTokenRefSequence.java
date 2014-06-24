/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import java.util.List;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.communications.TokenizedSuperCommunication;

/**
 * @author max
 *
 */
public class ValidatableTokenRefSequence extends AbstractAnnotation<TokenRefSequence> {

  /**
   * @param annotation
   */
  public ValidatableTokenRefSequence(TokenRefSequence annotation) {
    super(annotation);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    UUID tokUuid = this.annotation.getTokenizationId();
    List<Integer> tokenIdxIds = this.annotation.getTokenIndexList();

    TokenizedSuperCommunication cc = new TokenizedSuperCommunication(c);
    if (this.printStatus("Tokenization UUID must be an existing tokenization.", cc.getTokenizationIds().contains(tokUuid))) {
      Set<Integer> tokIdxSet = cc.getTokenizationIdToTokenIdxToTokenMap().get(tokUuid).keySet();
      if (this.printStatus("All token IDs must be present in the tokenization.", !tokIdxSet.containsAll(tokenIdxIds)))
        return false;
      else
        return true;
    } else {
      return false;
    }
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    UUID tokUuid = this.annotation.getTokenizationId();
    return this.validateUUID(tokUuid)
        && this.printStatus("TokenIndexList must be set", this.annotation.isSetTokenIndexList())
        && this.printStatus("TokenIndexList must have >0 items", this.annotation.getTokenIndexListSize() > 0);
  }

}
