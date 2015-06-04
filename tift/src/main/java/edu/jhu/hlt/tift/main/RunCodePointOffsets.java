/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.tift.Tokenizer;

/**
 * @author max
 *
 */
public class RunCodePointOffsets {

  private static final Logger logger = LoggerFactory.getLogger(RunCodePointOffsets.class);

  /**
     *
     */
  public RunCodePointOffsets() {
    // TODO Auto-generated constructor stub
  }

  /**
   * This function takes 2 arguments:
   *
   * <pre>
   * 1) path to a file of text, and
   * 2) path to a file of tokens, one per line
   * </pre>
   *
   * This function reads the text into a string, reads the tokens into a List of Strings, then outputs code point offsets for the text-token combination into a
   * file called "output.txt".
   *
   * @param args
   *          - length 2 array with a path to the original text, and a path to token file, one per line
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      logger.info("Usage: RunCodePointOffsets [path/to/original/text] [path/to/tokens]");
      System.exit(1);
    }

    File inputTextFile = new File(args[0]);
    File inputTokenFile = new File(args[1]);

    StringBuilder sb = new StringBuilder();

    try (Scanner sc = new Scanner(inputTextFile, StandardCharsets.UTF_8.toString());) {
      while (sc.hasNextLine())
        sb.append(sc.nextLine());
    }

    List<String> tokenList = new ArrayList<>();
    try (Scanner sc = new Scanner(inputTokenFile, StandardCharsets.UTF_8.toString());) {
      while (sc.hasNextLine())
        tokenList.add(sc.nextLine());
    }

    int[] codePointOffsets = Tokenizer.getOffsets(sb.toString(), tokenList);
    try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("output.txt"), StandardCharsets.UTF_8)) {
      for (int i = 0; i < codePointOffsets.length; i++) {
        bw.write(codePointOffsets[i]);
        bw.write(System.lineSeparator());
      }
    }
  }
}
