/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import java.util.List;

import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;

public class TokenizationUtils {
  public static enum TagTypes {
    POS,
    NER,
    LEMMA
  };

  public TokenizationUtils() {
  }

  // TOKEN TAGGINGS
  /**
   * Find the first POS tagging (TokenTagging.taggingType == POS) in the
   * given {@link Tokenization}. This throws a {@link ConcreteException}
   * if no POS taggings are found.
   * Note that this is <em>only</em> to be used when the exact POS theory 
   * doesn't matter, or when the caller is sure that there is only one
   * POS theory.
   */
  public TokenTagging getFirstPOSTags(Tokenization tokenization) throws ConcreteException {
    return getFirstXTags(tokenization, TagTypes.POS);
  }

  /**
   * Find the first LEMMA tagging (TokenTagging.taggingType == LEMMA) in the
   * given {@link Tokenization}. This throws a {@link ConcreteException}
   * if no LEMMA taggings are found.
   * Note that this is <em>only</em> to be used when the exact LEMMA theory 
   * doesn't matter, or when the caller is sure that there is only one
   * LEMMA theory.
   */
  public TokenTagging getFirstLemmaTags(Tokenization tokenization) throws ConcreteException {
    return getFirstXTags(tokenization, TagTypes.LEMMA);
  }

  /**
   * Find the first NER tagging (TokenTagging.taggingType == NER) in the
   * given {@link Tokenization}. This throws a {@link ConcreteException}
   * if no NER taggings are found.
   * Note that this is <em>only</em> to be used when the exact NER theory 
   * doesn't matter, or when the caller is sure that there is only one
   * NER theory.
   */
  public TokenTagging getFirstNERTags(Tokenization tokenization) throws ConcreteException {
    return getFirstXTags(tokenization, TagTypes.NER);
  }
  private TokenTagging getFirstXTags(Tokenization tokenization, TagTypes which) throws ConcreteException {
    if (!tokenization.isSetTokenTaggingList())
      throw new ConcreteException("No TokenTaggings for tokenization: " + tokenization.getUuid());
    
    List<TokenTagging> tokenTaggingLists = tokenization.getTokenTaggingList();
    for(int i = 0; i < tokenTaggingLists.size(); i++) {
      TokenTagging tt = tokenTaggingLists.get(i);
      if(tt.isSetTaggingType() && tt.getTaggingType().equals(which.name()))
        return tt;
    }
    
    throw new ConcreteException("Did not find any tag theories with taggingType == " + which +" in tokenization " + tokenization.getUuid());
  }

  /**
   * Find the first POS tagging (TokenTagging.taggingType == POS) in the
   * given {@link Tokenization} whose tool name <em>contains</em> 
   * toolName. This throws a {@link ConcreteException}
   * if no POS taggings containing the desired toolname are found.
   */
  public TokenTagging getFirstPOSTagsWithName(Tokenization tokenization, String toolName) throws ConcreteException {
    return getFirstXTagsWithName(tokenization, TagTypes.POS, toolName);
  }

  /**
   * Find the first LEMMA tagging (TokenTagging.taggingType == LEMMA) in the
   * given {@link Tokenization} whose tool name <em>contains</em> 
   * toolName. This throws a {@link ConcreteException}
   * if no LEMMA taggings containing the desired toolname are found.
   */
  public TokenTagging getFirstLemmaTagsWithName(Tokenization tokenization, String toolName) throws ConcreteException {
    return getFirstXTagsWithName(tokenization, TagTypes.LEMMA, toolName);
  }

  /**
   * Find the first NER tagging (TokenTagging.taggingType == NER) in the
   * given {@link Tokenization} whose tool name <em>contains</em> 
   * toolName. This throws a {@link ConcreteException}
   * if no NER taggings containing the desired toolname are found.
   */
  public TokenTagging getFirstNERTagsName(Tokenization tokenization, String toolName) throws ConcreteException {
    return getFirstXTagsWithName(tokenization, TagTypes.NER, toolName);
  }
  
  private TokenTagging getFirstXTagsWithName(Tokenization tokenization, TagTypes which,
                                             String toolName) throws ConcreteException {
    if (!tokenization.isSetTokenTaggingList())
      throw new ConcreteException("No TokenTaggings for tokenization: " + tokenization.getUuid());
    
    List<TokenTagging> tokenTaggingLists = tokenization.getTokenTaggingList();
    for(int i = 0; i < tokenTaggingLists.size(); i++) {
      TokenTagging tt = tokenTaggingLists.get(i);
      if(tt.isSetTaggingType() && 
         tt.getTaggingType().equals(which.name()) &&
         tt.getMetadata().getTool().contains(toolName))
        return tt;
    }
    
    throw new ConcreteException("Did not find any tag theories with taggingType == " + which +" in tokenization " + tokenization.getUuid() + " with toolname containing " + toolName);
  }

  // DEPENDENCY PARSES
  /**
   * Find the first DependencyParse in the
   * given {@link Tokenization} whose tool name <em>contains</em> 
   * toolName. This throws a {@link ConcreteException} if no 
   * dependency parses containing the desired toolname are found.
   */  
  public DependencyParse getFirstDependencyParseWithName(Tokenization tokenization, String toolName) throws ConcreteException {
    if (!tokenization.isSetDependencyParseList())
      throw new ConcreteException("No DependencyParses for tokenization: " + tokenization.getUuid());
    
    List<DependencyParse> parseList = tokenization.getDependencyParseList();
    for (int i = 0; i < parseList.size(); i++) {
      DependencyParse dp = parseList.get(i);
      if(dp.getMetadata().getTool().contains(toolName))
        return dp;
    }

    throw new ConcreteException("Did not find any  dependency parses containing the string \""+toolName +"\" in tokenization " + tokenization.getUuid());
  }
}
