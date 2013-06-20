/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.EntityMention;
import edu.jhu.hlt.concrete.Concrete.Token;
import edu.jhu.hlt.concrete.Concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.Concrete.Tokenization;

/**
 * Wrapper around {@link EntityMention} object. 
 * 
 * @author max
 *
 */
public class ConcreteEntityMention {

    private final EntityMention mention;
    private final Communication comm;
    
    /**
     * 
     */
    public ConcreteEntityMention(EntityMention mention, Communication comm) {
        this.mention = mention;
        this.comm = comm;
    }
    
    /**
     * Return a {@link List} of {@link Token} objects in the first {@link Tokenization} object in
     * this {@link Communication} object. If none exists, will throw an exception.
     * 
     * @return a List of Token objects
     */
    public List<Token> getTokens() {
        List<Token> tokenList;
        // Makes the following assumptions:
        // * You want the first section segmentation,
        // * You want the first section,
        // * You want the first sentence segmentation,
        // * You want the first sentence, and
        // * You want the first tokenization. 
        Tokenization tkn = comm.getSectionSegmentation(0)
                                .getSection(0)
                                .getSentenceSegmentation(0)
                                .getSentence(0)
                                .getTokenization(0);
        TokenRefSequence trs = this.mention.getTokens();
        List<Integer> tokenIdList = trs.getTokenIdList();
        tokenList = new ArrayList<>(tokenIdList.size());
        for (Integer i : tokenIdList)
            tokenList.add(tkn.getToken(i));
        
        return tokenList;
    }
    
    /**
     * Return a {@link List} of {@link String}s that represent tokens.
     * 
     * @return a List of Strings that are the tokens in this tokenization
     */
    public List<String> getTokensAsStrings() {
        List<String> stringList;
        List<Token> tokenList = this.getTokens();
        stringList = new ArrayList<>(tokenList.size());
        for (Token tok : tokenList)
            stringList.add(tok.getText());
        
        return stringList;
    }
}
