/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.ConcreteException;

public class TokenListIndexedTokenization extends IndexedTokenization {
  // ======================================================================
  // Private variables
  // ======================================================================

  /** Mapping from tokenid to position in the token list. Lazily constructed. */
  private int[] tokenIdToPos = null;

  // ======================================================================
  // Constructor
  // ======================================================================

  /* package-private */TokenListIndexedTokenization(Concrete.Tokenization tokenization, ProtoIndex index) throws ConcreteException {
    super(tokenization, index);
  }

  // ======================================================================
  // Indexing
  // ======================================================================

  @Override
  protected void updateIndices() throws ConcreteException {
    super.updateIndices();
    // For now, take the lazy way out: throw everything away and
    // recompute from scratch.
    tokenIdToPos = null;
  }

  // ======================================================================
  // One-best, n-best
  // ======================================================================

  @Override
  public TokenSequence getBestTokenSequence() {
    return new TokenSequenceImpl(protoObj.getTokenList(), 0);
  }

  @Override
  public List<TokenSequence> getNBestTokenSequences(int n) throws ConcreteException {
    List<TokenSequence> result = new ArrayList<TokenSequence>(Math.max(0, n));
    if (n > 0)
      result.add(getBestTokenSequence());
    return result;
  }

  @Override
  public Iterator<TokenSequence> iterTokenSequences() throws ConcreteException {
    return getNBestTokenSequences(1).iterator();
  }

  // ======================================================================
  // IndexedTokenization method implementations
  // ======================================================================

  @Override
  public Concrete.Token getNextToken(int tokenId) {
    if (tokenIdToPos == null)
      buildTokenIdToPos();
    int nextPos = tokenIdToPos[tokenId] + 1;
    if (nextPos < protoObj.getTokenCount())
      return protoObj.getToken(nextPos);
    else
      return null;
  }

  @Override
  public Concrete.Token getPrevToken(int tokenId) {
    if (tokenIdToPos == null)
      buildTokenIdToPos();
    int prevPos = tokenIdToPos[tokenId] - 1;
    if (prevPos >= 0)
      return protoObj.getToken(prevPos);
    else
      return null;
  }

  private void buildTokenIdToPos() {
    int max_tokid = 0;
    for (Concrete.Token tok : protoObj.getTokenList())
      max_tokid = Math.max(max_tokid, tok.getTokenIndex());
    tokenIdToPos = new int[max_tokid + 1];
    for (int i = 0; i < protoObj.getTokenCount(); ++i) {
      tokenIdToPos[i] = protoObj.getToken(i).getTokenIndex();
    }
  }

  @Override
  protected Map<Integer, Concrete.Token> buildTokenIndex() {
    Map<Integer, Concrete.Token> tokenIndex = new HashMap<Integer, Concrete.Token>();
    for (Concrete.Token tok : protoObj.getTokenList())
      tokenIndex.put(tok.getTokenIndex(), tok);
    return tokenIndex;
  }

}
