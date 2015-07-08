/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.tift;

/**
 * 2-tuple that contains a token and a tag.
 */
public class TokenTagTuple {

    private final String token;
    private final String tag;
    
    /**
     * 
     */
    public TokenTagTuple(String token, String tag) {
        this.token = token;
        this.tag = tag;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

}
