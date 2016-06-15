/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift;

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
 */
public class TaggedTokenizationOutput {

  private final String[] tokens;
  private final String[] tokenTags;
  private final int[] offsets;

  public TaggedTokenizationOutput(String[][] twitterTokenizerOutput) {
    this.tokens = twitterTokenizerOutput[0];
    this.tokenTags = twitterTokenizerOutput[1];
    String[] offsetStrings = twitterTokenizerOutput[2];
    this.offsets = new int[offsetStrings.length];
    for (int i = 0; i < offsetStrings.length; i++)
      this.offsets[i] = Integer.parseInt(offsetStrings[i]);
  }

  /**
   * @return the tokens
   */
  public String[] getTokens() {
    return tokens;
  }

  /**
   * @return the tokenTags
   */
  public String[] getTokenTags() {
    return tokenTags;
  }

  /**
   * @return the offsets
   */
  public int[] getOffsets() {
    return offsets;
  }
}
