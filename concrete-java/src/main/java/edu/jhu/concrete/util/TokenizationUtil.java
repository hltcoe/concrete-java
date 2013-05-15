/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.concrete.util;

import java.util.Arrays;
import java.util.List;

import edu.jhu.concrete.Concrete;

/**
 * Utility class for {@link Concrete.Tokenization} related code.
 * 
 * @author max
 *
 */
public class TokenizationUtil {

    /**
     * 
     */
    private TokenizationUtil() {

    }

    /**
     * Wrapper around generateConcreteTokenization() that takes an array of
     * Strings (tokens).
     * 
     * @see #generateConcreteTokenization(List, int[], int)
     * 
     * @param tokens
     *            - an array of tokens (Strings)
     * @param offsets
     *            - an array of integers (offsets)
     * @param startPos
     *            - starting position of the text
     * @return a {@link Concrete.Tokenization} object with correct tokenization
     */
    public static Concrete.Tokenization generateConcreteTokenization(String[] tokens, int[] offsets, int startPos) {
        return generateConcreteTokenization(Arrays.asList(tokens), offsets, startPos);
    }
    
    /**
     * Generate a {@link Concrete.Tokenization} object from a list of tokens,
     * list of offsets, and start position of the text (e.g., first text
     * character in the text).
     * 
     * @param tokens
     *            - a {@link List} of tokens (Strings)
     * @param offsets
     *            - an array of integers (offsets)
     * @param startPos
     *            - starting position of the text
     * @return a {@link Concrete.Tokenization} object with correct tokenization
     */
    public static Concrete.Tokenization generateConcreteTokenization(List<String> tokens, int[] offsets, int startPos) {
        Concrete.Tokenization.Builder tokenizationBuilder = Concrete.Tokenization.newBuilder().setUuid(IdUtil.generateUUID())
                .setKind(Concrete.Tokenization.Kind.TOKEN_LIST);
        // Note: we use token index as token id.
        for (int tokenId = 0; tokenId < tokens.size(); ++tokenId) {
            String token = tokens.get(tokenId);
            int start = startPos + offsets[tokenId];
            int end = start + token.length();
            tokenizationBuilder.addTokenBuilder().setTokenId(tokenId).setText(token)
                    .setTextSpan(Concrete.TextSpan.newBuilder().setStart(start).setEnd(end));
        }

        return tokenizationBuilder.build();
    }
    
}
