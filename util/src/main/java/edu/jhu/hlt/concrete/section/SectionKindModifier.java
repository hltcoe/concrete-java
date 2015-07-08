/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.section;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Modify all sections in a {@link Communication} object to be of a certain type.
 */
public class SectionKindModifier {

  private static final Logger logger = LoggerFactory.getLogger(SectionKindModifier.class);

  /**
   * See usage string.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 3) {
      System.err.println("This program converts a Concrete Communication file with sections to a "
          + "Concrete Communication file with sections of the provided type.");
      System.err.println("The .concrete file will share the same name as the input file, including the extension.");
      System.err.println("This program takes 3 arguments.");
      System.err.println("Argument 1: path/to/a/concrete/communication/file/with/sections");
      System.err.println("Argument 2: type of Section to generate [e.g., passage]");
      System.err.println("Argument 3: path/to/output/folder");
      System.err.println("Example usage: " + SectionKindModifier.class.getName()
          + " /my/comm/file.concrete Passage /my/output/folder");
      System.exit(1);
    }

    final String inPathStr = args[0];
    final String sectionKind = args[1];
    Path inPath = Paths.get(inPathStr);
    try {
      ExistingNonDirectoryFile ef = new ExistingNonDirectoryFile(inPath);
      Optional<String> outPathStr = Optional.ofNullable(args[2]);

      String fn = ef.getName();
      Path outPath = Paths.get(outPathStr.get());
      Path outFile = outPath.resolve(fn);

      // Output directory exists, or it doesn't.
      // Try to create if it does not.
      if (!Files.exists(outPath)) {
        try {
          Files.createDirectories(outPath);
        } catch (IOException e) {
          logger.error("Caught exception when making output directories.", e);
        }

      // if it does, check to make sure it's a directory.
      } else {
        if (!Files.isDirectory(outPath)) {
          logger.error("Output path exists but is not a directory.");
          System.exit(1);
        } else {
          // check to make sure the output file won't be overwritten.
          if (Files.exists(outFile)) {
            logger.warn("Output file {} exists; not overwriting.", outFile.toString());
            System.exit(1);
          }
        }
      }

      try {
        Communication comm = new CompactCommunicationSerializer().fromPath(inPath);
        // Do not run over sectioned comms.
        if (!comm.isSetSectionList() || comm.getSectionListSize() <= 0) {
          logger.error("Communication has no sections. Not running.");
          System.exit(1);
        }

        Communication nc = new Communication(comm);
        for (Section s : nc.getSectionList())
          s.setKind(sectionKind);
        new WritableCommunication(nc).writeToFile(outFile, false);
      } catch (ConcreteException e) {
        logger.error("Caught exception writing output.", e);
        System.exit(1);
      }

    } catch (NoSuchFileException e) {
      logger.error("Path {} does not exist.", inPathStr);
      System.exit(1);
    } catch (NotFileException e) {
      logger.error("Path {} is a directory.", inPathStr);
      System.exit(1);
    }
  }
}
