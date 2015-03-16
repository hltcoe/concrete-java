/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;

/**
 * Messy wrapper around {@link TokenTagging} allowing some crude validation.
 */
public class ValidatableTokenTagging {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ValidatableTokenTagging.class);
  
  private final TokenTagging tagging;
  private final Tokenization parent;
  
  private final List<TaggedToken> tokenTaggings;
  private final List<Integer> ttIndices;
  private final int maxTTIndex;

  private final List<Integer> tokIndices;
  private final int maxTokenIdx; 
  
  /**
   * @param annotation
   */
  public ValidatableTokenTagging(TokenTagging tagging, Tokenization parent) {
    this.tagging = tagging;
    this.parent = parent;
    
    // TODO: only accept correct Tokenization
    TokenizationKind kind = parent.getKind();
    switch (kind) {
    case TOKEN_LIST:
      TokenList tok = parent.getTokenList();
      List<Token> tokList = tok.getTokenList();
      List<Integer> tokIndicesList = new ArrayList<Integer>();
      int tmpIdx = -1;
      for (Token t : tokList) {
        final int tidx = t.getTokenIndex();
        tokIndicesList.add(tidx);
        if (tmpIdx < tidx)
          tmpIdx = tidx;  
      }
      
      this.maxTokenIdx = tmpIdx;
      this.tokIndices = tokIndicesList;
      break;
    default:
      throw new IllegalArgumentException("Validating of tokenization type: " + parent.getKind() + " not supported.");
    }
    
    List<TaggedToken> ttList = this.tagging.getTaggedTokenList();
    this.tokenTaggings = ttList;
    if (ttList.size() > 0) {
      this.ttIndices = new ArrayList<Integer>();
      
      int tmpMaxIdx = -1;
      for (TaggedToken tt : ttList) {
        int ttIndex = tt.getTokenIndex();
        if (tmpMaxIdx < ttIndex)
          tmpMaxIdx = ttIndex;
        this.ttIndices.add(tt.getTokenIndex());
      }
      
      this.maxTTIndex = tmpMaxIdx;
    } else {
      this.ttIndices = new ArrayList<>();
      this.maxTTIndex = -1;
    }
  }
  
  public boolean maxTokenTaggingIndexLTEMaxTokenIndex() {
    // Max TokenTagging index cannot be higher than max token index.
    return this.maxTTIndex <= this.maxTokenIdx;
  }

  public boolean isValid() {
    LOGGER.debug("Testing validity of TokenTagging: {}", this.tagging.getUuid().toString());
    if (!this.maxTokenTaggingIndexLTEMaxTokenIndex()) {
      LOGGER.info("Maximum TokenTagging index cannot be larger than maximum token index.");
      LOGGER.info("Highest TokenTagging index: {}", this.maxTTIndex);
      LOGGER.info("Highest Token index: {}", this.maxTokenIdx);
      return false;
    }
    
    return true;
  }
}
