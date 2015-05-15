/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.tokenized.CachedTokenizationCommunication;

/**
 *
 */
public class ValidatableTokenRefSequence extends AbstractAnnotation<TokenRefSequence> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidatableTokenRefSequence.class);

  /**
   * @param annotation
   */
  public ValidatableTokenRefSequence(TokenRefSequence annotation) {
    super(annotation);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    UUID tokUuid = this.annotation.getTokenizationId();
    List<Integer> tokenIdxIds = this.annotation.getTokenIndexList();

    try {
      CachedTokenizationCommunication cc = new CachedTokenizationCommunication(c);

      if (this.printStatus("Tokenization UUID must be an existing tokenization.", cc.getUuidToTokenizationMap().keySet().contains(tokUuid))) {
        Set<Integer> tokIdxSet = cc.getUuidToTokenIdxToTokenMap().get(tokUuid).keySet();
        return this.printStatus("All token IDs must be present in the tokenization.", tokIdxSet.containsAll(tokenIdxIds));
      } else {
        return false;
      }
    } catch (MiscommunicationException e) {
      LOGGER.error("Caught an exception creating the convenience wrapper.", e);
      return false;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    UUID tokUuid = this.annotation.getTokenizationId();
    return this.validateUUID(tokUuid) && this.printStatus("TokenIndexList must be set", this.annotation.isSetTokenIndexList())
        && this.printStatus("TokenIndexList must have >0 items", this.annotation.getTokenIndexListSize() > 0);
  }
}
