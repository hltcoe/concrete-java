/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.tokenization;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.SuperTextSpan;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 *
 */
public class PrintCommunicationTokens {

  private static final Logger logger = LoggerFactory.getLogger(PrintCommunicationTokens.class);

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("This program prints the tokens of a single Concrete Communication file.");
      System.err.println("The Communication must have at least one tokenization.");
      System.err.println("This program only prints tokenizations with type TokenList.");
      System.err.println("It takes one argument:");
      System.err.println("Argument 1: /path/to/concrete/communication/with/tokenization");
      System.exit(1);
    }

    String inPathStr = args[0];
    Path inPath = Paths.get(inPathStr);
    try {
      new ExistingNonDirectoryFile(inPath);
      final Communication c = new CompactCommunicationSerializer().fromPath(inPath);
      if (!c.isSetSectionList() || c.getSectionListSize() <= 0) {
        logger.error("Communication did not have sections.");
        System.exit(1);
      }

      List<Sentence> sentList = new ArrayList<>();
      for (Section s : c.getSectionList())
        if (s.isSetSentenceList())
          sentList.addAll(s.getSentenceList());

      if (sentList.isEmpty()) {
        logger.error("No sentences found in the sections, so no tokenizations were available.");
        System.exit(1);
      }

      List<Tokenization> tokList = new ArrayList<>();
      for (Sentence s : sentList)
        if (s.isSetTokenization())
          tokList.add(s.getTokenization());

      Stream<Tokenization> tokListStream = tokList.stream()
          .filter(t -> t.getKind() == TokenizationKind.TOKEN_LIST);
      tokListStream.forEach(t -> {
        final String uids = t.getUuid().getUuidString();
        if (!t.isSetTokenList())
          logger.warn("Tokenization {} is invalid. Its type is TOKEN_LIST, but the TokenList is unset.", uids);
        else {
          TokenList tl = t.getTokenList();
          if (!tl.isSetTokenList())
            logger.warn("Tokenization {} is invalid. Its type is TOKEN_LIST, but it has no list of Tokens.", uids);
          else {
            logger.info("Tokenization {} tokens:", uids);
            tl.getTokenList().stream().forEach(tk -> {
              logger.info("Token #{}: {}", tk.getTokenIndex(), new SuperTextSpan(tk.getTextSpan(), c).getText());
            });
          }
        }
      });

    } catch (NoSuchFileException e) {
      logger.error("Path {} does not exist.", inPathStr);
      System.exit(1);
    } catch (NotFileException e) {
      logger.error("Path {} is a directory.", inPathStr);
      System.exit(1);
    } catch (ConcreteException e) {
      logger.error("Exception processing Concrete content.", e);
    }
  }
}
