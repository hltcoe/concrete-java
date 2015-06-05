package edu.jhu.hlt.tift.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.tift.Tokenizer;

public class AnnoPipelineCommand {

  private static final Logger logger = LoggerFactory.getLogger(AnnoPipelineCommand.class);

  /**
     *
     */
  public AnnoPipelineCommand() {
    // TODO Auto-generated constructor stub
  }

  /*
   * @param args
   *
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      logger.info("Usage: AnnoPipelineCommand [tokenization_type] [path/to/text/file]");
    }

    String tokenizationType = args[0];
    Tokenizer tokenizer = null;

    for (Tokenizer curTokenizer : Tokenizer.class.getEnumConstants()) {
      if (curTokenizer.name().equalsIgnoreCase(tokenizationType))
        tokenizer = curTokenizer;
    }

    if (tokenizer == null) {
      logger.info("Invalid Tokenization Type: " + args[0] + "\n Must be <PTB>,<WHITESPACE>,<TWITTER_PETROVIC>,<TWITTER>, or <BASIC>"
          + "\n Default go with: BASIC");
      tokenizer = Tokenizer.BASIC;
    }

    File input = new File(args[1]);
    try (Scanner sc = new Scanner(input, StandardCharsets.UTF_8.toString());) {
      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        if (line.matches("^<.*>$"))
          System.out.println(line);
        else {
          StringBuilder sb = new StringBuilder();
          List<String> tokenList = tokenizer.tokenize(line);
          for (String tok : tokenList) {
            sb.append(tok);
            sb.append(" ");
          }

          System.out.println(sb.toString());
        }
      }
    }
  }
}
