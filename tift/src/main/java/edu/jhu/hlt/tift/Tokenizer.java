/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.tift;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.tift.ConcreteTokenization;

/**
 * Enumeration of supported tokenizations.
 */
public enum Tokenizer {

  PTB {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      return generateConcreteTokenization(this, text, textStartPosition);
    }

    @Override
    public List<String> tokenize(String text) {
      return Arrays.asList(Rewriter.PTB.rewrite(text).split("\\s+"));
    }

    @Override
    public Tokenization tokenizeSentence(String text, int textStartPosition, UUID sentUuid) {
      return generateConcreteTokenization(this, text, textStartPosition, sentUuid);
    }
  },
  WHITESPACE {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      return generateConcreteTokenization(this, text, textStartPosition);
    }

    @Override
    public Tokenization tokenizeSentence(String text, int textStartPosition, UUID sentUuid) {
      return generateConcreteTokenization(this, text, textStartPosition, sentUuid);
    }

    @Override
    public List<String> tokenize(String text) {
      return Arrays.asList(text.split("\\s+"));
    }
  },
  TWITTER_PETROVIC {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      return generateConcreteTokenization(this, text, textStartPosition);
    }

    @Override
    public List<String> tokenize(String text) {
      return tokenizeTweetPetrovic(text);
    }

    @Override
    public Tokenization tokenizeSentence(String text, int textStartPosition, UUID sentUuid) {
      return generateConcreteTokenization(this, text, textStartPosition, sentUuid);
    }
  },
  TWITTER {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      TaggedTokenizationOutput tto = TwitterTokenizer.tokenize(text);
      return ConcreteTokenization.generateConcreteTokenization(tto);
    }

    @Override
    public List<String> tokenize(String text) {
      return TwitterTokenizer.tokenize(text).getTokens();
    }

    @Override
    public Tokenization tokenizeSentence(String text, int textStartPosition, UUID sentUuid) {
      return generateConcreteTokenization(this, text, textStartPosition, sentUuid);
    }
  },
  BASIC {
    @Override
    public Tokenization tokenizeToConcrete(String text, int textStartPosition) {
      return generateConcreteTokenization(this, text, textStartPosition);
    }

    @Override
    public List<String> tokenize(String text) {
      return Arrays.asList(Rewriter.BASIC.rewrite(text).split("\\s+"));
    }

    @Override
    public Tokenization tokenizeSentence(String text, int textStartPosition, UUID sentUuid) {
      return generateConcreteTokenization(this, text, textStartPosition, sentUuid);
    }
  };

  //
  // Contract methods.
  //
  public abstract Tokenization tokenizeToConcrete(String text, int textStartPosition);
  public abstract Tokenization tokenizeSentence(String text, int textStartPosition, UUID sentUuid);

  public abstract List<String> tokenize(String text);

  //
  // Patterns & sets of patterns.
  //

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
  public static int[] getOffsets(String text, String[] tokens) {
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
    List<String> content = new ArrayList<String>();

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

      if (update || ((i == (length - 1)) && (!token.equals("")))) {
        content.add(token);
        update = false;
      }
    }

    return content;
  }

  /**
   * Wrapper around getOffsets that takes a {@link List} of Strings instead of an array.
   *
   * @see #getOffsets(String, String[])
   *
   * @param text
   *          - text that was tokenized
   * @param tokenList
   *          - a {@link List} of tokenized text
   * @return an array of integers that represent offsets
   */
  public static int[] getOffsets(String text, List<String> tokenList) {
    return getOffsets(text, tokenList.toArray(new String[0]));
  }

  public static Tokenization generateConcreteTokenization(Tokenizer tokenizationType, String text, int startPosition, UUID sentUuid) {
    return generateConcreteTokenization(tokenizationType, text, startPosition)
        .setUuid(sentUuid);
  }

  public static Tokenization generateConcreteTokenization(Tokenizer tokenizationType, String text, int startPosition) {
    List<String> tokenList = tokenizationType.tokenize(text);
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
