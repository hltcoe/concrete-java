/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.tift;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.jhu.hlt.tift.PatternStringTuple;

/**
 * Enumeration of available "text rewriting" tools. 
 */
public enum Rewriter {
  PTB {
    @Override
    public String rewrite(String text) {
      return Rewriter.rewrite(text, PTB_PATTERNS);
    }
  },
  BASIC {
    @Override
    public String rewrite(String text) {
      return Rewriter.rewrite(text, BASIC_PATTERNS);
    }
  },
  COMMON_UNICODE {
    @Override
    public String rewrite(String text) {
      return Rewriter.rewrite(text, COMMON_UNICODE_PATTERNS);
    }
  };

  public abstract String rewrite(String text);

  public static final Set<PatternStringTuple> PTB_PATTERNS = getPTBPatterns();
  public static final Set<PatternStringTuple> BASIC_PATTERNS = getBasicPatterns();
  public static final Set<PatternStringTuple> COMMON_UNICODE_PATTERNS = getCommonUnicodePatterns();

    private static Set<PatternStringTuple> getCommonUnicodePatterns () {
        // vandurme: I went through the top 100 unicode characters in a large
        // collection of Spanish tweets, looking for the most common things we would
        // want to rewrite. For the most useful that resulted, below are the
        // frequencies, the unicode, a suggested mapping, and a text description.
        // 88740 \u201c " double-quote
        // 78883 \u201d " right-double-quote
        // 55270 \u2665 <3 black-heart
        // 33534 \u2014 - EM dash
        // 29702 \u263a :) smiley face
        // 20527 \u2026 ... horizontal ellipsis
        // 12903 \u0336 - COMBINING LONG STROKE OVERLAY
        // 11983 \u2588 || full block
        // 11684 \u2639 :( white frowning face
        // 11490 \u00a0   no break space
        // 10251 \ud83d poo-symbol pile of poo
        // 8362 \u266b music-symbol beamed eighth notes
        // 8254 \u2591 light-shade-symbol LIGHT SHADE
        // 7201 \u00bb >> RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
        // 7189 \u2022 * bullet
        // 7035 \u00b0 o degree sign
        // 6990 \u266a music-symbol eighth-note
        // 6801 \u2013 - en dash
        // 6515 \u2019 ' single right quotation
        // 6230 \u0338 / COMBINING LONG SOLIDUS OVERLAY
        // 5387 \u00ab << LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
        // 4049 \u2508 ---- BOX DRAWINGS LIGHT QUADRUPLE DASH HORIZONTAL
        // 3933 \u2501 - BOX DRAWINGS HEAVY HORIZONTAL
        // 3736 \u2001   EM QUAD
        // 3665 \u2003   EM SPACE
        // 3594 \u25b8 => BLACK RIGHT-POINTING SMALL TRIANGLE
        // 3544 \u200b   zero width space
        // 3499 \u2500 - BOX DRAWINGS LIGHT HORIZONTAL
        // 3474 \u2611 checkmark-symbol  BALLOT BOX WITH CHECK
        // 3349 \u2503 | BOX DRAWINGS HEAVY VERTICAL
        // 2958 \ud83c game-die-symbol
        // 2884 \u2580 ^ UPPER HALF BLOCK
        // #2833 \u20ac euro-symbol Euro symbol
        // 2744 \u2018 ' single left quotation
        // 2722 \u2661 <3 white heart
        // 2690 \u2605 star-symbol black star
        // 2534 \u2600 sun-symbol BLACK SUN WITH RAYS 
        // 2346 \u2550 = BOX DRAWINGS DOUBLE HORIZONTAL
        // 2094 \u0305 - COMBINING OVERLINE

        String[] p = {
                "\u201c", "\"",
                "\u201d", "\"", 
                "\u2665", "<3",
                "\u2014", "-",
                "\u263a", ":)",
                "\u2026", "...",
                "\u0336", "-",
                "\u2588", "||",
                "\u2639", ":(",
                "\u00a0", " ",
                "\ud83d", " poo-symbol ",
                "\u266b", " music-symbol ",
                "\u2591", " light-shade-symbol ",
                "\u00bb", "==>",
                "\u300b", "==>", // based on example in twokenize example tweets
                "\u2022", "*",
                "\u00b0", "o",
                "\u266a", " music-symbol ",
                "\u2013", "-",
                "\u2019", "\'",
                "\u0338", "/",
                "\u00ab", "<==",
                "\u2508", "----",
                "\u2501", "-",
                "\u2001", " ",
                "\u2003", " ",
                "\u25b8", "==>",
                "\u200b", " ",
                "\u2500", "-",
                "\u2611", " checkmark-symbol ",
                "\u2503", "|",
                "\ud83c", " gamedie-symbol ",
                "\u2580", "^",
                "\u2018", "\'",
                "\u2661", "<3",
                "\u2605", " star-symbol ",
                "\u2600", " sun-symbol ",
                "\u2550", "=",
                "\u0305", "-" };

        return convertStringArrayPatternsToTupleSet(p);
    }


    /**
     * A conservative version of the PTB patterns, meant to (hopefully) be
     * portable across formal/informal Western (?) languages.
     */
    public static Set<PatternStringTuple> getBasicPatterns() {
        // cut-n-paste, then modified from getPTBPatterns
        String[] v = {
                // double quotes
                "([\"\u201c\u201d\u201e\u201f\u275d\u275e])", " $1 ",

                // Ellipsis
                "\\.\\.\\.", " ... ",

                "([,;:@#$%&\\*])", " $1 ",

                // HTML escaped (stop gap)
                "& ([gl])t ;", "&$1t;",
                "& nbsp ;", " &nbsp; ",
                "& hearts ;", " &hearts; ",

                // vandurme: carefully with final .
                "([^\\.])(\\.)(\\s|$)", "$1 $2$3",


                // however, we may as well split ALL question marks and exclamation
                // points, since they shouldn't have the abbrev.-marker ambiguity
                // problem.
                //"([\\?!])", " $1 ",
                // vandurme> adding unicode characters
                // \u00a1 : ! inverted
                // \u00bf : ? inverted
                "([\\?!\u00a1\u00bf])", " $1 ",

                // parentheses, brackets, etc.
                "([\\]\\[\\(\\){}<>])", " $1 ",

                "--", " -- "
        };

        return convertStringArrayPatternsToTupleSet(v);
    }

    /**
     * Based on inspection of:
     * 
     * http://www.cis.upenn.edu/~treebank/tokenizer.sed
     * 
     * The header of which identifies the author as:
     * "Robert MacIntyre, University of Pennsylvania, late 1995".
     */
    public static Set<PatternStringTuple> getPTBPatterns() {
        // The following is a port of patterns and comments from tokenizer.sed
        String[] v = {
                // attempt to get correct forward directional quotes, close quotes
                // handled at end
                "^\"", "`` ",
                "([ \\(\\[{<])\"", "$1 `` ",

                "\\.\\.\\.", "...",
                "([,;:@#$%&])", " $1 ",

                // Assume sentence tokenization has been done first, so split FINAL
                // periods only. (vandurme: WARNING this is often not true for us)
                "([^\\.])([\\.])([\\]\\)}>\"']*) *$", "$1 $2$3 ",

                // however, we may as well split ALL question marks and exclamation
                // points, since they shouldn't have the abbrev.-marker ambiguity
                // problem
                "([\\?!])", " $1 ",

                // parentheses, brackets, etc.
                "([\\]\\[\\(\\){}<>])", " $1 ",

                "--", " -- ",

                // NOTE THAT SPLIT WORDS ARE NOT MARKED. Obviously this isn't great,
                // since you might someday want to know how the words originally fit
                // together -- but it's too late to make a better system now, given
                // the millions of words we've already done "wrong".

                // First off, add a space to the beginning and end of each line, to reduce
                // necessary number of regexps.
                "$", " ",
                "^", " ",

                // (vandurme: this is the closing quotation MacIntyre refers to earlier)
                "\"", " '' ",

                // possessive or close-single-quote
                "([^'])' ", "$1 ' ",

                // as in it's, I'm, we'd
                "'([sSmMdD])", " '$1 ",

                "'ll ", " 'll ",
                "'re ", " 're ",
                "'ve ", " 've ",
                "n't ", " n't ",
                "'LL ", " 'LL ",
                "'RE ", " 'RE ",
                "'VE ", " 'VE ",
                "N'T ", " N'T ",

                " ([Cc])annot ", " $1an not",
                " ([Dd])'ye ", " $1' ye",
                " ([Gg])imme ", " $1im me ",
                " ([Gg])onna ", " $1on na ",
                " ([Gg])otta ", " $1ot ta ",
                " ([Ll])emme ", " $1em me ",
                " ([Mm])ore'n ", " $1ore 'n ",
                " ('[Tt])is ", " $1 is ",
                " ('[Tt])was ", " $1 was ",
                " ([Ww])anna ", " $1an na ",
                //" ([Ww])haddya ", " $1ha dd ya ",
                //" ([Ww]hatcha ", " $1ha t cha ",

                // clean out extra spaces
                " +", " ",
                "^ +", ""
        };

        return convertStringArrayPatternsToTupleSet(v);
    }

  private static Set<PatternStringTuple> convertStringArrayPatternsToTupleSet(String[] patternArray) {
    Set<PatternStringTuple> patterns = new HashSet<PatternStringTuple>(patternArray.length);
    for (int i = 0; i < patternArray.length - 1; i += 2)
      patterns.add(new PatternStringTuple(Pattern.compile(patternArray[i], Pattern.MULTILINE), patternArray[i + 1]));

    return Collections.unmodifiableSet(patterns);
  }

  private static String rewrite(String text, Set<PatternStringTuple> patterns) {
    String x = text;
    for (PatternStringTuple pair : patterns)
      x = pair.getPattern().matcher(x).replaceAll(pair.getEntry());

    return x.trim();
  }
}
