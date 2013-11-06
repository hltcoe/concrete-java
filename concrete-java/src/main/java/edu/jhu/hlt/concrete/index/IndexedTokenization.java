/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.index;

/* Things to add...
 - n-best
 - bag of words
 - bag of ngrams
 - prev-token(s)
 - next-token(s)
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.util.IdUtil;

/**
 * A wrapper for a Tokenization objects that makes them easier to use. In particular, this wrapper provides:
 * 
 * - Automatic indexing of tokens by id - Automatic indexing of POS taggings - Methods to extract the best token sequence - Methods to extract bags of words or
 * bags of ngrams
 * 
 * Two subclasses, one for lattices??
 * 
 */
abstract public class IndexedTokenization extends IndexedProto<Concrete.Tokenization> {
  // ======================================================================
  // Private variables
  // ======================================================================

  /** Index of tokens, by token id (lazily constructed) */
  private Map<Integer, Concrete.Token> tokenIndex = null;

  /** Index of token taggings by uuid+tokenId (lazily constructed) */
  private Map<Concrete.UUID, Map<Integer, String>> tokenTagIndex = null;

  /**
   * Index of token taggings, by tool name (lazily constructed). Shares values with tokenTagIndex.
   */
  private Map<String, Map<Integer, String>> taggingsByToolName = null;

  // ======================================================================
  // Constructor
  // ======================================================================

  public static IndexedTokenization build(Concrete.UUID uuid, ProtoIndex index) throws ConcreteException {
    IndexedTokenization cached = index.getIndexedProto(uuid);
    if (cached != null)
      return cached;
    else
      return build((Concrete.Tokenization) index.lookup(uuid), index);
  }

  public static IndexedTokenization build(Concrete.Tokenization tokenization, ProtoIndex index) throws ConcreteException {
    IndexedTokenization cached = index.getIndexedProto(tokenization.getUuid());
    if (cached != null)
      return cached;
    if (tokenization.getKind() == Concrete.Tokenization.Kind.TOKEN_LIST)
      return new TokenListIndexedTokenization(tokenization, index);
    else if (tokenization.getKind() == Concrete.Tokenization.Kind.TOKEN_LATTICE)
      return new TokenLatticeIndexedTokenization(tokenization, index);
    else
      throw new ConcreteException("Unexpected tokenization.kind " + tokenization.getKind());
  }

  protected IndexedTokenization(Concrete.Tokenization tokenization, ProtoIndex index) throws ConcreteException {
    super(tokenization, index);
  }

  // ======================================================================
  // Static Token Lookup
  // ======================================================================

  public static Concrete.Token getToken(Concrete.TokenRef ref, ProtoIndex index) throws ConcreteException {
    return build(ref.getTokenizationId(), index).getToken(ref.getTokenIndex());
  }

  public static List<Concrete.Token> getTokens(Concrete.TokenRefSequence ref, ProtoIndex index) throws ConcreteException {
    IndexedTokenization tokz = build(ref.getTokenizationId(), index);
    List<Concrete.Token> result = new ArrayList<Concrete.Token>(ref.getTokenIndexCount());
    for (int tokid : ref.getTokenIndexList())
      result.add(tokz.getToken(tokid));
    return result;
  }

  // ======================================================================
  // Indexing
  // ======================================================================

  @Override
  protected void updateIndices() throws ConcreteException {
    super.updateIndices();
    // For now, take the lazy way out: throw everything away and
    // recompute from scratch.
    tokenIndex = null;
    tokenTagIndex = null;
    taggingsByToolName = null;
  }

  // ======================================================================
  // One-best, n-best
  // ======================================================================

  public interface TokenSequence extends List<Concrete.Token> {
    double getWeight();
  }

  abstract public TokenSequence getBestTokenSequence() throws ConcreteException;

  abstract public List<TokenSequence> getNBestTokenSequences(int n) throws ConcreteException;

  abstract public Iterator<TokenSequence> iterTokenSequences() throws ConcreteException;

  protected static class TokenSequenceImpl extends ArrayList<Concrete.Token> implements TokenSequence {
    private final double weight;

    @Override
    public double getWeight() {
      return weight;
    }

    TokenSequenceImpl(double weight) {
      this.weight = weight;
    }

    TokenSequenceImpl(List<Concrete.Token> tokens, double weight) {
      super(tokens);
      this.weight = weight;
    }
  }

  // ======================================================================
  // Next/Prev Token
  // ======================================================================

  /**
   * Return the token that follows the specified token, or null if no such token exists. In the case of token lattices, return the token that is most likely to
   * follow this token. If two tokens are equally likely to follow this token, then it is undefined which one is returned.
   */
  abstract public Concrete.Token getNextToken(int tokenId) throws ConcreteException;

  public Concrete.Token getNextToken(Concrete.Token token) throws ConcreteException {
    return getNextToken(token.getTokenIndex());
  }

  /**
   * Return the token that precedes the specified token, or null if no such token exists. In the case of token lattices, return the token that is most likely to
   * precede this token. If two tokens are equally likely to precede this token, then it is undefined which one is returned.
   */
  abstract public Concrete.Token getPrevToken(int tokenId) throws ConcreteException;

  public Concrete.Token getPrevToken(Concrete.Token token) throws ConcreteException {
    return getPrevToken(token.getTokenIndex());
  }

  // ======================================================================
  // Token Lookup
  // ======================================================================

  /** Return the token with the given token identifier. */
  public Concrete.Token getToken(int tokenId) {
    if (tokenIndex == null)
      tokenIndex = buildTokenIndex();
    return tokenIndex.get(tokenId);
  }

  /**
   * Construct and return a map from tokenid to token for this tokenization.
   */
  abstract protected Map<Integer, Concrete.Token> buildTokenIndex();

  // ======================================================================
  // Tag Lookup
  // ======================================================================

  /**
   * Return the tag assigned to the specified token by the indicated tagging. If no tagging with the given UUID is found, then raise an exception. If the
   * tagging does not assign any tag to the given token, then return null.
   */
  // public String getTag(int tokenId, Concrete.UUID taggingUuid) throws ConcreteException {
  // return getTokenTagIndex(taggingUuid).get(tokenId); }
  // public String getTag(Concrete.Token token, Concrete.UUID taggingUuid) throws ConcreteException {
  // return getTag(token.getTokenId(), taggingUuid); }

  /**
   * Return the tag assigned to the specified token by the unique tagging with the given tool name. If there are no such taggings or multiple such taggings,
   * then throw a ConcreteException. If the tagging does not assign any tag to the given token, then return null.
   */
  // public String getTag(int tokenId, String toolName) throws ConcreteException {
  // return getTokenTagIndex(toolName).get(tokenId); }
  // public String getTag(Concrete.Token token, String toolName) throws ConcreteException {
  // return getTag(token.getTokenId(), toolName); }

  /**
   * Return the tag assigned to the specified token. This method requires that the tokenization have a single tagging; if not, then it will throw a
   * ConcreteException.
   */
  // public String getTag(int tokenId) throws ConcreteException {
  // if (protoObj.getTaggingCount() == 0)
  // throw new ConcreteException("No tagging found for this tokenization.");
  // if (protoObj.getTaggingCount() > 1)
  // throw new ConcreteException("The IndexedTokenization.getTag(int) method requires that "+
  // "the tokenization have a single tagging; use one of: "+
  // "getTag(UUID, int), getTag(String, int), or "+
  // "getTag(String,String,int) to specify which tagging to use");
  // return getTag(tokenId, protoObj.getTagging(0).getUuid());
  // }
  // public String getTag(Concrete.Token token) throws ConcreteException {
  // return getTag(token.getTokenId());
  // }

  /**
   * Return a map from token-id to tag, extracted from the TokenTagging with the given UUID
   */
  // public Map<Integer, String> getTagMap(Concrete.UUID uuid) throws ConcreteException {
  // return Collections.unmodifiableMap(getTokenTagIndex(uuid));
  // }

  /** Private helper -- look up a tag index by tool name. */
  // private Map<Integer, String> getTokenTagIndex(String toolName) throws ConcreteException {
  // if (taggingsByToolName == null)
  // taggingsByToolName = new HashMap<String, Map<Integer, String>>();
  // Map<Integer, String> tagIndex = taggingsByToolName.get(toolName);
  // if (tagIndex != null) {
  // return tagIndex;
  // } else {
  // Map<Integer, String> result = null;
  // for (Concrete.TokenTagging tagging: protoObj.getTaggingList()) {
  // if (toolName.equals(tagging.getMetadata().getTool())) {
  // if (result != null)
  // throw new ConcreteException("Multiple taggings have the tool name "+toolName);
  // result = buildTokenTagIndex(tagging);
  // }
  // }
  // taggingsByToolName.put(toolName, result);
  // return result;
  // }
  // }

  /** Private helper -- look up a tag index by UUID. */
  // private Map<Integer, String> getTokenTagIndex(Concrete.UUID uuid) throws ConcreteException {
  // if (tokenTagIndex==null)
  // tokenTagIndex = new HashMap<Concrete.UUID, Map<Integer, String>>();
  // Map<Integer, String> tagIndex = tokenTagIndex.get(uuid);
  // if (tagIndex != null) {
  // return tagIndex;
  // } else {
  // for (Concrete.TokenTagging tagging: protoObj.getTaggingList()) {
  // if (IdUtil.getUUID(tagging) == uuid)
  // return buildTokenTagIndex(tagging);
  // }
  // }
  // throw new ConcreteException("This tokenization does not contain a tagging "+
  // "with the specified UUID.");
  // }

  /** Private helper -- build a tag index. */
  private Map<Integer, String> buildTokenTagIndex(Concrete.TokenTagging tagging) throws ConcreteException {
    HashMap<Integer, String> tagIndex = new HashMap<Integer, String>();
    for (Concrete.TokenTagging.TaggedToken ttok : tagging.getTaggedTokenList())
      tagIndex.put(ttok.getTokenIndex(), ttok.getTag());
    tokenTagIndex.put(IdUtil.getUUID(tagging), tagIndex);
    return tagIndex;
  }

}
