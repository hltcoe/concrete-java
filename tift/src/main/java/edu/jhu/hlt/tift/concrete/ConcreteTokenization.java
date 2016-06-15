/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift.concrete;

import java.util.Arrays;
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

  private ConcreteTokenization() {
  }

  public static Tokenization generateConcreteTokenization(List<String> tokens, int[] offsets, int startPos) {
    return generateConcreteTokenization(tokens.toArray(new String[tokens.size()]), offsets, startPos);
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
  public static Tokenization generateConcreteTokenization(String[] tokens, int[] offsets, int startPos) {
    Tokenization tkz = new Tokenization();
    tkz.setKind(TokenizationKind.TOKEN_LIST);
    tkz.setMetadata(new AnnotationMetadata(tiftMetadata));
    tkz.setUuid(UUIDFactory.newUUID());

    TokenList tl = new TokenList();
    // Note: we use token index as token id.
    for (int tokenId = 0; tokenId < tokens.length; ++tokenId) {
      String token = tokens[tokenId];
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
  public static Tokenization generateConcreteTokenization(String[] tokens, String[] tokenTags, int[] offsets, int startPos) {
    Tokenization tokenization = generateConcreteTokenization(tokens, offsets, startPos);
    TokenTagging tt = new TokenTagging();
    tt.setUuid(UUIDFactory.newUUID());
    tt.setTaggingType("POS");
    tt.setMetadata(new AnnotationMetadata(tiftMetadata));
    for (int i = 0; i < tokens.length; i++) {
      String tag = tokenTags[i];
      if (tag != null) {
        TaggedToken tok = new TaggedToken();
        tok.setTokenIndex(i).setTag(tokenTags[i]);
        tt.addToTaggedTokenList(tok);
      }
    }

    // Do not set the pos tags if everything was "null".
    if (tt.isSetTaggedTokenList())
      tokenization.addToTokenTaggingList(tt);

    return tokenization;
  }

  public static Tokenization generateConcreteTokenization(TaggedTokenizationOutput tto) {
    return generateConcreteTokenization(tto.getTokens(), tto.getTokenTags(), tto.getOffsets(), 0);
  }
}
