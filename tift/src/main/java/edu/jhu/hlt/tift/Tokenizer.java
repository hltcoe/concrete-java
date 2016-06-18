/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.tift.concrete.ConcreteTokenization;

/**
 * Enumeration of supported tokenizations.
 */
public enum Tokenizer {

  PTB {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      return generateConcreteTokenization(text, textStartPosition);
    }

    @Override
    public List<String> tokenize(String text) {
      return ImmutableList.copyOf(Rewriter.PTB.rewrite(text).split("\\s+"));
    }
  },
  WHITESPACE {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      return generateConcreteTokenization(text, textStartPosition);
    }

    @Override
    public List<String> tokenize(String text) {
      return ImmutableList.copyOf(text.split("\\s+"));
    }
  },
  TWITTER_PETROVIC {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      return generateConcreteTokenization(text, textStartPosition);
    }

    @Override
    public List<String> tokenize(String text) {
      return tokenizeTweetPetrovic(text);
    }
  },
  TWITTER {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      TaggedTokenizationOutput tto = TwitterTokenizer.tokenize(text);
      Tokenization tkz = ConcreteTokenization.generateConcreteTokenization(tto);
      final String tool = "Tift TwitterTokenizer " + ProjectConstants.VERSION;
      tkz.getMetadata().setTool("Tift TwitterTokenizer " + ProjectConstants.VERSION);
      if (tkz.isSetTokenTaggingList())
        tkz.getTokenTaggingListIterator().next().getMetadata().setTool(tool + " Tweet Tags");
      return tkz;
    }

    @Override
    public List<String> tokenize(String text) {
      return ImmutableList.copyOf(TwitterTokenizer.tokenize(text).getTokens());
    }
  },
  BASIC {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      return generateConcreteTokenization(text, textStartPosition);
    }

    @Override
    public List<String> tokenize(String text) {
      return ImmutableList.copyOf(Rewriter.BASIC.rewrite(text).split("\\s+"));
    }
  };

  //////////////////////////////////////////////////
  // Contract methods.
  //////////////////////////////////////////////////
  /**
   * Tokenize a {@link String}, given a character offset.
   *
   * @param text a {@link String} to tokenize
   * @param textStartPosition used to denote offsets with respect to the entire document.
   * For example, if you wish to tokenize the second sentence from the following text:
   * <pre>
   * He left. He returned later.
   * </pre>
   * call this method with parameters <code>He will return later.</code> and <code>9</code>.
   * @return a {@link Tokenization} corresponding to this {@link Tokenizer} instance
   *
   * @see #tokenizeToConcrete(String)
   */
  public abstract Tokenization tokenizeToConcrete(String text, int textStartPosition);

  public abstract List<String> tokenize(String text);

  /**
   * Tokenize a string.
   * <br><br>
   * For maintaining character offsets, see {@link #tokenizeToConcrete(String, int)}.
   *
   * @param text a {@link String} to tokenize
   * @return a {@link Tokenization} corresponding to this {@link Tokenizer} instance
   *
   * @see #tokenizeToConcrete(String, int)
   */
  public final Tokenization tokenizeToConcrete(String text) {
    return this.tokenizeToConcrete(text, 0);
  }

  //
  // Static methods.
  //
  /**
   * Return the offsets of tokens in text.
   *
   * @param text
   *          - text to be used
   * @param tokens
   * @return an integer array of offsets
   */
  static int[] getOffsets(String text, String[] tokens) {
    int[] r = new int[tokens.length];
    int x = 0;
    for (int i = 0; i < tokens.length; i++) {
      for (int j = x; j < text.length(); j++) {
        if (text.startsWith(tokens[i], j)) {
          r[i] = j;
          x = j + tokens[i].length();
          j = text.length();
        }
      }
    }
    return r;
  }

  /**
   * Sasa Petrovic's tokenization scheme.
   *
   * @param text
   *          - text to tokenize
   * @return a list of Strings that represent tokens.
   */
  static List<String> tokenizeTweetPetrovic(String text) {
    int length = text.length();
    int state = 0;
    String token = "";
    char c;
    int cType;
    boolean update = false;
    ImmutableList.Builder<String> content = new ImmutableList.Builder<>();

    // My (vandurme) one change was to add UPPERCASE_LETTER as another
    // option alongside LOWER_CASE_LETTER
    for (int i = 0; i < length; i++) {
      c = text.charAt(i);
      cType = Character.getType(c);

      switch (state) {
      case 0: // Start state
        token = "";
        if (cType == Character.SPACE_SEPARATOR)
          break;
        // link
        // Characters matched out of order to fail
        // early when not a link.
        else if ((c == 'h') && (i + 6 < length) && (text.charAt(i + 4) == ':') && (text.charAt(i + 5) == '/')) {
          token += c;
          state = 4;
          break;
        }
        // normal
        else if ((cType == Character.LOWERCASE_LETTER) || (cType == Character.UPPERCASE_LETTER) || (cType == Character.DECIMAL_DIGIT_NUMBER)) {
          token += c;
          state = 1;
          break;
        }
        // @reply
        else if (c == '@') {
          token += c;
          state = 2;
          break;
        }
        // #topic
        else if (c == '#') {
          token += c;
          state = 3;
          break;
        } else
          break;
      case 1: // Normal
        if ((cType == Character.LOWERCASE_LETTER) || (cType == Character.UPPERCASE_LETTER) || (cType == Character.DECIMAL_DIGIT_NUMBER)) {
          token += c;
          break;
        } else {
          update = true;
          state = 0;
          break;
        }
      case 2: // @reply
        // Author names may have underscores,
        // which we don't want to split on here
        if ((cType == Character.LOWERCASE_LETTER) || (cType == Character.UPPERCASE_LETTER) || (cType == Character.DECIMAL_DIGIT_NUMBER) || (c == '_')) {
          token += c;
          break;
        } else {
          update = true;
          state = 0;
          break;
        }
      case 3: // #topic
        // This could just be state 1, with special care
        // taken in state 0 when the topic is first
        // recognized, but I'm staying aligned to Sasa's
        // code
        if ((cType == Character.LOWERCASE_LETTER) || (cType == Character.UPPERCASE_LETTER) || (cType == Character.DECIMAL_DIGIT_NUMBER)) {
          token += c;
          break;
        } else {
          update = true;
          state = 0;
          break;
        }
      case 4: // link
        if ((cType == Character.SPACE_SEPARATOR) || (c == '[')) {
          // if ((c == ' ') || (c == '[')) {
          update = true;
          state = 0;
          break;
        } else {
          token += c;
          break;
        }

      default:
        // nothing
        break;
      }

      if (update || ((i == (length - 1)) && (!token.isEmpty()))) {
        content.add(token);
        update = false;
      }
    }

    return content.build();
  }

  /**
   * Wrapper around getOffsets that takes a {@link List} of Strings instead of an array.
   *
   * @see #getOffsets(String, String[])
   *
   * @param text
   *          text that was tokenized
   * @param tokenList
   *          a {@link List} of tokenized text
   * @return an array of integers that represent offsets
   */
  static int[] getOffsets(String text, List<String> tokenList) {
    return getOffsets(text, tokenList.toArray(new String[0]));
  }

  Tokenization generateConcreteTokenization(String text, int startPosition) {
    List<String> tokenList = this.tokenize(text);
    int[] offsets = getOffsets(text, tokenList);
    return ConcreteTokenization.generateConcreteTokenization(tokenList, offsets, startPosition);
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("expects 2 arguments: tokenizer-type filename");
      System.exit(1);
    }

    Tokenizer t = Tokenizer.valueOf(args[0].toUpperCase());
    try (BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "UTF-8"));) {
      String line;
      List<String> toks;
      while ((line = b.readLine()) != null) {
        toks = t.tokenize(line);
        if (toks.size() > 0) {
          System.out.print(toks.get(0));
          for (int i = 1; i < toks.size(); i++)
            System.out.print(toks.get(i) + " ");
          System.out.println();
        }
      }
    }
  }

}
