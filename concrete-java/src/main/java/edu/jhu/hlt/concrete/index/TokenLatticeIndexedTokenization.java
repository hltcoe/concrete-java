/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.Concrete.Tokenization.TokenLattice.Arc;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.util.MathUtil;

public class TokenLatticeIndexedTokenization extends IndexedTokenization {
  // ======================================================================
  // Private variables
  // ======================================================================

  /** Map token id to arc */
  private Map<Integer, Arc> tokenArcs = null;
  /** Map state id to outgoing arcs */
  private Map<Integer, List<Arc>> outgoingArcs = null;
  /** Map state id to incoming arcs */
  private Map<Integer, List<Arc>> incomingArcs = null;
  /** Sum of costs of all paths from start state to a given state */
  private Map<Integer, Double> fwdCost = null;
  /** Sum of costs of all paths from a given state to the end state */
  private Map<Integer, Double> bkwCost = null;
  /** Cost of minimum-cost path from start state to a given state */
  private Map<Integer, Double> minFwdCost = null;
  /** Cost of minimum-cost path from a given state to the end state */
  private Map<Integer, Double> minBkwCost = null;
  /** Toplogically sorted list of state ids */
  private List<Integer> topoSortedStates = null;

  // ======================================================================
  // Constructor
  // ======================================================================

  /* package-private */TokenLatticeIndexedTokenization(Concrete.Tokenization tokenization, ProtoIndex index) throws ConcreteException {
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
    tokenArcs = null;
    outgoingArcs = null;
    incomingArcs = null;
  }

  // ======================================================================
  // One-best, n-best
  // ======================================================================

  @Override
  public TokenSequence getBestTokenSequence() throws ConcreteException {
    Concrete.Tokenization.TokenLattice lattice = protoObj.getLattice();
    if (lattice.hasCachedBestPath())
      return new TokenSequenceImpl(lattice.getCachedBestPath().getTokenList(), lattice.getCachedBestPath().getWeight());
    // Find minimum costs to reach each arc from the start state
    initMinFwdCost();
    // Walk backwards from the end state
    int state = lattice.getEndState();
    TokenSequenceImpl result = new TokenSequenceImpl(minFwdCost.get(state));
    while (state != lattice.getStartState()) {
      // Find the best arc to here.
      double bestCost = Double.POSITIVE_INFINITY;
      Arc bestArc = null;
      for (Arc arc : getIncomingArcs(state)) {
        double newCost = minFwdCost.get(arc.getSrc()) + arc.getWeight();
        if (newCost < bestCost) {
          bestCost = newCost;
          bestArc = arc;
        }
      }
      // Add the arc's token if it has one.
      if (bestArc.hasToken())
        result.add(bestArc.getToken());
      state = bestArc.getSrc();
    }
    // Reverse the tokens (since we walked from back-to-front)
    Collections.reverse(result);
    return result;
  }

  @Override
  public List<TokenSequence> getNBestTokenSequences(int n) throws ConcreteException {
    if (n > 1)
      throw new ConcreteException("getNBestTokenSequences(n) with n>1 not implemented yet for lattices.");
    List<TokenSequence> result = new ArrayList<TokenSequence>(Math.max(0, n));
    if (n == 1)
      result.add(getBestTokenSequence());
    return result;
  }

  @Override
  public Iterator<TokenSequence> iterTokenSequences() throws ConcreteException {
    throw new ConcreteException("iterTokenSequences() not implemented yet for lattices.");
  }

  // ======================================================================
  // IndexedTokenization method implementations
  // ======================================================================

  @Override
  public Concrete.Token getNextToken(int tokenId) throws ConcreteException {
    if (tokenArcs == null)
      indexArcs();
    return getNextToken(tokenArcs.get(tokenId).getDst(), 0, new WeightedToken()).token;
  }

  @Override
  public Concrete.Token getPrevToken(int tokenId) throws ConcreteException {
    if (tokenArcs == null)
      indexArcs();
    return getPrevToken(tokenArcs.get(tokenId).getDst(), 0, new WeightedToken()).token;
  }

  @Override
  protected Map<Integer, Concrete.Token> buildTokenIndex() {
    final Map<Integer, Concrete.Token> tokenIndex = new HashMap<Integer, Concrete.Token>();
    for (Arc arc : protoObj.getLattice().getArcList()) {
      if (arc.hasToken()) {
        final Concrete.Token tok = arc.getToken();
        tokenIndex.put(tok.getTokenIndex(), tok);
      }
    }
    return tokenIndex;
  }

  // ======================================================================
  // Helpers
  // ======================================================================

  /** helper data class for getNextToken() and getPrevToken(). */
  private static final class WeightedToken {
    public Concrete.Token token = null;
    public double weight = Double.POSITIVE_INFINITY;
  }

  private WeightedToken getNextToken(int state, double weight, WeightedToken result) {
    Concrete.Token bestTok = null;
    for (Arc arc : getOutgoingArcs(state)) {
      if (arc.hasToken()) {
        if (arc.getWeight() < result.weight) {
          result.token = arc.getToken();
          result.weight = arc.getWeight() + weight;
        }
      } else { // epsilon edge
        getNextToken(arc.getDst(), weight + arc.getWeight(), result);
      }
    }
    return result;
  }

  private WeightedToken getPrevToken(int state, double weight, WeightedToken result) {
    Concrete.Token bestTok = null;
    for (Arc arc : getIncomingArcs(state)) {
      if (arc.hasToken()) {
        if (arc.getWeight() < result.weight) {
          result.token = arc.getToken();
          result.weight = arc.getWeight() + weight;
        }
      } else { // epsilon edge
        getPrevToken(arc.getDst(), weight + arc.getWeight(), result);
      }
    }
    return result;
  }

  /** Initializes outgoingArcs, incomingArcs, and tokenArcs */
  private void indexArcs() throws ConcreteException {
    if (tokenArcs != null)
      return;
    outgoingArcs = new HashMap<Integer, List<Arc>>();
    incomingArcs = new HashMap<Integer, List<Arc>>();
    tokenArcs = new HashMap<Integer, Arc>();
    for (Arc a : protoObj.getLattice().getArcList()) {
      if (!outgoingArcs.containsKey(a.getSrc()))
        outgoingArcs.put(a.getSrc(), new ArrayList<Arc>());
      outgoingArcs.get(a.getSrc()).add(a);
      if (!incomingArcs.containsKey(a.getDst()))
        incomingArcs.put(a.getDst(), new ArrayList<Arc>());
      incomingArcs.get(a.getDst()).add(a);
      if (a.hasToken())
        if (tokenArcs.put(a.getToken().getTokenIndex(), a) != null)
          throw new ConcreteException("Multiple tokens have the same id " + a.getToken().getTokenIndex());
    }
  }

  /**
   * Initialize topoSortedState to be a topologically sorted list of the states in this lattice.
   */
  private void initTopoSortedStates() throws ConcreteException {
    if (topoSortedStates != null)
      return;
    indexArcs();
    topoSortedStates = new ArrayList<Integer>();
    topoVisit(protoObj.getLattice().getEndState(), new HashSet<Integer>());
    topoSortedStates = Collections.unmodifiableList(topoSortedStates);
  }

  /** Helper for initTopoSortedStates */
  private void topoVisit(int state, Set<Integer> visited) {
    if (visited.add(state))
      return;
    for (Arc arc : getOutgoingArcs(state))
      topoVisit(arc.getSrc(), visited);
    topoSortedStates.add(state);
  }

  private void initFwdCost() throws ConcreteException {
    Concrete.Tokenization.TokenLattice lattice = protoObj.getLattice();
    if (fwdCost != null)
      return;
    initTopoSortedStates();
    // Initialize the map to have infintite costs
    fwdCost = new HashMap<Integer, Double>();
    for (int state : topoSortedStates)
      fwdCost.put(state, Double.POSITIVE_INFINITY);
    // Propagate costs forward.
    fwdCost.put(lattice.getStartState(), 0.0);
    for (int state : topoSortedStates) {
      double costSoFar = fwdCost.get(state);
      for (Arc arc : getOutgoingArcs(state)) {
        double newCost = -MathUtil.addLogs(-fwdCost.get(arc.getDst()), -(costSoFar + arc.getWeight()));
        fwdCost.put(arc.getDst(), newCost);
      }
    }
  }

  private void initBkwCost() throws ConcreteException {
    Concrete.Tokenization.TokenLattice lattice = protoObj.getLattice();
    if (bkwCost != null)
      return;
    initTopoSortedStates();
    // Initialize the map to have infintite costs
    bkwCost = new HashMap<Integer, Double>();
    for (int state : topoSortedStates)
      bkwCost.put(state, Double.POSITIVE_INFINITY);
    // Propagate costs backward.
    bkwCost.put(lattice.getStartState(), 0.0);
    for (int i = topoSortedStates.size() - 1; i >= 0; i--) {
      int state = topoSortedStates.get(i);
      double costSoFar = bkwCost.get(state);
      for (Arc arc : getIncomingArcs(state)) {
        double newCost = -MathUtil.addLogs(-bkwCost.get(arc.getSrc()), -(costSoFar + arc.getWeight()));
        bkwCost.put(arc.getSrc(), newCost);
      }
    }
  }

  private void initMinFwdCost() throws ConcreteException {
    Concrete.Tokenization.TokenLattice lattice = protoObj.getLattice();
    if (minFwdCost != null)
      return;
    initTopoSortedStates();
    // Initialize the map to have infintite costs
    minFwdCost = new HashMap<Integer, Double>();
    for (int state : topoSortedStates)
      minFwdCost.put(state, Double.POSITIVE_INFINITY);
    // Propagate costs forward.
    minFwdCost.put(lattice.getStartState(), 0.0);
    for (int state : topoSortedStates) {
      double costSoFar = minFwdCost.get(state);
      for (Arc arc : getOutgoingArcs(state)) {
        double newCost = Math.min(minFwdCost.get(arc.getDst()), (costSoFar + arc.getWeight()));
        minFwdCost.put(arc.getDst(), newCost);
      }
    }
  }

  private void initMinBkwCost() throws ConcreteException {
    Concrete.Tokenization.TokenLattice lattice = protoObj.getLattice();
    if (minBkwCost != null)
      return;
    initTopoSortedStates();
    // Initialize the map to have infintite costs
    minBkwCost = new HashMap<Integer, Double>();
    for (int state : topoSortedStates)
      minBkwCost.put(state, Double.POSITIVE_INFINITY);
    // Propagate costs backward.
    minBkwCost.put(lattice.getStartState(), 0.0);
    for (int i = topoSortedStates.size() - 1; i >= 0; i--) {
      int state = topoSortedStates.get(i);
      double costSoFar = minBkwCost.get(state);
      for (Arc arc : getIncomingArcs(state)) {
        double newCost = Math.min(minBkwCost.get(arc.getSrc()), (costSoFar + arc.getWeight()));
        minBkwCost.put(arc.getSrc(), newCost);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private List<Arc> getIncomingArcs(int state) {
    List<Arc> arcs = incomingArcs.get(state);
    return (arcs == null) ? Collections.EMPTY_LIST : arcs;
  }

  @SuppressWarnings("unchecked")
  private List<Arc> getOutgoingArcs(int state) {
    List<Arc> arcs = outgoingArcs.get(state);
    return (arcs == null) ? Collections.EMPTY_LIST : arcs;
  }

}
