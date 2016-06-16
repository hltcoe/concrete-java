/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tift;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;
import edu.jhu.hlt.tift.TaggedTokenizationOutput;

/**
 * Utility class for {@link Tokenization} related code.
 */
public class ConcreteTokenization {

  private static final AnnotationMetadata tiftMetadata;

  static {
    AnnotationMetadata am = new AnnotationMetadata();
    am.setTimestamp(Timing.currentLocalTime());
    am.setTool("Tift " + ProjectConstants.VERSION);
    tiftMetadata = new AnnotationMetadata(am);
  }

  public static final AnnotationMetadata getMetadata() {
    return new AnnotationMetadata(tiftMetadata);
  }

  /**
     *
     */
  private ConcreteTokenization() {
    // TODO Auto-generated constructor stub
  }

  /**
   * Wrapper around {@link #generateConcreteTokenization(List, int[], int)} that takes an array of Strings (tokens).
   *
   * @see #generateConcreteTokenization(List, int[], int)
   *
   * @param tokens
   *          - an array of tokens (Strings)
   * @param offsets
   *          - an array of integers (offsets)
   * @param startPos
   *          - starting position of the text
   * @return a {@link Tokenization} object with correct tokenization
   */
  public static Tokenization generateConcreteTokenization(String[] tokens, int[] offsets, int startPos) {
    return generateConcreteTokenization(Arrays.asList(tokens), offsets, startPos);
  }

  /**
   * Generate a {@link Tokenization} object from a list of tokens, list of offsets, and start position of the text (e.g., first text character in the text).
   *
   * @param tokens
   *          - a {@link List} of tokens (Strings)
   * @param offsets
   *          - an array of integers (offsets)
   * @param startPos
   *          - starting position of the text
   * @return a {@link Tokenization} object with correct tokenization
   */
  public static Tokenization generateConcreteTokenization(List<String> tokens, int[] offsets, int startPos) {
    Tokenization tkz = new Tokenization();
    tkz.setKind(TokenizationKind.TOKEN_LIST);
    tkz.setMetadata(new AnnotationMetadata(tiftMetadata));
    tkz.setUuid(UUIDFactory.newUUID());

    TokenList tl = new TokenList();
    // Note: we use token index as token id.
    for (int tokenId = 0; tokenId < tokens.size(); ++tokenId) {
      String token = tokens.get(tokenId);
      int start = startPos + offsets[tokenId];
      int end = start + token.length();
      TextSpan ts = new TextSpan(start, end);
      Token tokenObj = new Token();
      tokenObj.setTextSpan(ts).setText(token).setTokenIndex(tokenId);
      tl.addToTokenList(tokenObj);
    }

    tkz.setTokenList(tl);
    return tkz;
  }

  /**
   * Wrapper for {@link #generateConcreteTokenization(List, int[], int)} that takes a {@link List} of {@link Integer} objects.
   *
   * @see #generateConcreteTokenization(List, int[], int)
   *
   * @param tokens
   *          - a {@link List} of tokens (Strings)
   * @param offsets
   *          a {@link List} of offsets (Integer objects)
   * @param startPos
   *          - starting position of the text
   * @return a {@link Tokenization} object with correct tokenization
   */
  public static Tokenization generateConcreteTokenization(List<String> tokens, List<Integer> offsets, int startPos) {
    return generateConcreteTokenization(tokens, convertIntegers(offsets), startPos);
  }

  /**
   * Generate a {@link Tokenization} object from a list of tokens, list of tags, list of offsets, and start position of the text (e.g., first text character in
   * the text). Assumes tags are part of speech tags.
   *
   * Invokes {@link #generateConcreteTokenization(List, int[], int)} then adds tagging.
   *
   * @see #generateConcreteTokenization(List, int[], int)
   *
   * @param tokens
   *          - a {@link List} of tokens (Strings)
   * @param offsets
   *          - an array of integers (offsets)
   * @param startPos
   *          - starting position of the text
   * @return a {@link Tokenization} object with correct tokenization and token tagging
   */
  public static Tokenization generateConcreteTokenization(List<String> tokens, List<String> tokenTags, int[] offsets, int startPos) {
    Tokenization tokenization = generateConcreteTokenization(tokens, offsets, startPos);
    TokenTagging tt = new TokenTagging();
    tt.setUuid(UUIDFactory.newUUID());
    tt.setTaggingType("POS");
    tt.setMetadata(new AnnotationMetadata(tiftMetadata));
    for (int i = 0; i < tokens.size(); i++) {
      String tag = tokenTags.get(i);
      if (tag != null) {
        TaggedToken tok = new TaggedToken();
        tok.setTokenIndex(i).setTag(tokenTags.get(i));
        tt.addToTaggedTokenList(tok);
      }
    }

    // Do not set the pos tags if everything was "null".
    if (tt.isSetTaggedTokenList())
      tokenization.addToTokenTaggingList(tt);

    return tokenization;
  }

  public static Tokenization generateConcreteTokenization(TaggedTokenizationOutput tto) {
    return generateConcreteTokenization(tto.getTokens(), tto.getTokenTags(), convertIntegers(tto.getOffsets()), 0);
  }

  /**
   * Convert a {@link List} of {@link Integer} objects to an integer array primitive.
   *
   * Will throw a {@link NullPointerException} if any element in the list is null.
   *
   * @param integers
   *          a {@link List} of {@link Integer} objects, none of which are <code>null</code>
   * @return a primitive array of ints
   */
  public static int[] convertIntegers(List<Integer> integers) {
    int[] ret = new int[integers.size()];
    Iterator<Integer> iterator = integers.iterator();
    for (int i = 0; i < ret.length; i++)
      ret[i] = iterator.next().intValue();

    return ret;
  }
}
