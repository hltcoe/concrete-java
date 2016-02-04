/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;

/**
 * Exposes a main method for batch conversion of .sgml files (e.g.,
 * via <code>xargs</code> input).
 */
public class GigawordBatchDocumentConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(GigawordBatchDocumentConverter.class);

  public static void main (String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    if (args.length < 2) {
      LOGGER.info("This program takes 2 arguments:");
      LOGGER.info("The first is a path to an output directory, where the communications will be written.");
      LOGGER.info("The second is a path to the .sgml file to be ingested.");
      LOGGER.info("Additional arguments will ingest additional files (e.g., from xargs).");
      LOGGER.info("Usage: {} {} {}", GigawordBatchDocumentConverter.class.getName(), "/path/to/output/file", "/path/to/input/sgml/file", "<other/path/to/input/sgml/files>");
      System.exit(1);
    }

    Path outPath = Paths.get(args[0]);
    Optional.ofNullable(outPath.getParent()).ifPresent(p -> {
      if (!Files.exists(p))
        try {
          Files.createDirectories(p);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
    });

    if (!Files.isDirectory(outPath)) {
      LOGGER.error("Output path must be a directory.");
      System.exit(1);
    }

    GigawordDocumentConverter ing = new GigawordDocumentConverter();
    for (int i = 1; i < args.length; i++) {
      Path lp = Paths.get(args[i]);
      LOGGER.info("On path: {}", lp.toString());
      try {
        Communication c = ing.fromPath(lp);
        new WritableCommunication(c).writeToFile(outPath.resolve(c.getId() + ".comm"), true);
      } catch (ConcreteException | IOException e) {
        LOGGER.error("Caught exception during ingest on file: " + args[i], e);
      }
    }
  }
}
