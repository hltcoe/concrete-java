/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A data structure that has the following elements:
 * 
 * <pre>
 * List of tokens
 * List of token tags
 * List of offsets
 * </pre>
 * 
 * Initially created as a wrapper around the data structure returned by the twitter tokenizer.
 * 
 * @author max
 * 
 */
public class TaggedTokenizationOutput {

  private final List<String> tokens;
  private final List<String> tokenTags;
  private final List<Integer> offsets;

  /**
     * 
     */
  public TaggedTokenizationOutput(List<String> tokens, List<String> tokenTags, List<Integer> offsets) {
    this.tokens = tokens;
    this.tokenTags = tokenTags;
    this.offsets = offsets;
  }

  public TaggedTokenizationOutput(String[][] twitterTokenizerOutput) {
    this.tokens = Arrays.asList(twitterTokenizerOutput[0]);
    this.tokenTags = Arrays.asList(twitterTokenizerOutput[1]);
    String[] offsetStrings = twitterTokenizerOutput[2];
    this.offsets = new ArrayList<>(offsetStrings.length);
    for (String offset : offsetStrings)
      this.offsets.add(Integer.parseInt(offset));
  }

  /**
   * @return the tokens
   */
  public List<String> getTokens() {
    return tokens;
  }

  /**
   * @return the tokenTags
   */
  public List<String> getTokenTags() {
    return tokenTags;
  }

  /**
   * @return the offsets
   */
  public List<Integer> getOffsets() {
    return offsets;
  }

}
