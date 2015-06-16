/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;

/**
 * Class that is able to process the .gz files associated with the LDC release
 * of English Gigaword v5 into Concrete.
 */
public class GigawordGzProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(GigawordGzProcessor.class);

  /**
   *
   */
  private GigawordGzProcessor() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    if (args.length != 2) {
      LOGGER.info("This program takes 2 arguments.");
      LOGGER.info("First: the path to a .gz file that is part of the English Gigaword v5 corpus.");
      LOGGER.info("Second: the path to the output file (a .tar.gz with communication files).");
      LOGGER.info("Example usage:");
      LOGGER.info("{} {} {}", GigawordGzProcessor.class.getName(), "/path/to/LDC/sgml/.gz", "/path/to/out.tar.gz");
      System.exit(1);
    }

    String inPathStr = args[0];
    String outPathStr = args[1];

    Path inPath = Paths.get(inPathStr);
    if (!Files.exists(inPath))
      LOGGER.error("Input path {} does not exist. Try again with the right path.", inPath.toString());

    Path outPath = Paths.get(outPathStr);
    Optional<Path> parent = Optional.ofNullable(outPath.getParent());
    // lambda does not allow caught exceptions.
    if (parent.isPresent()) {
      if (!Files.exists(outPath.getParent())) {
        LOGGER.info("Attempting to create output directory: {}", outPath.toString());
        try {
          Files.createDirectories(outPath);
        } catch (IOException e) {
          LOGGER.error("Caught exception creating output directory.", e);
        }
      }
    }

    GigawordDocumentConverter conv = new GigawordDocumentConverter();
    Iterator<Communication> iter = conv.gzToStringIterator(inPath);
    try (OutputStream os = Files.newOutputStream(outPath);
        BufferedOutputStream bos = new BufferedOutputStream(os, 1024 * 8 * 16);
        GzipCompressorOutputStream gout = new GzipCompressorOutputStream(bos);
        TarArchiver archiver = new TarArchiver(gout);) {
      while (iter.hasNext()) {
        Communication c = iter.next();
        LOGGER.info("Adding Communication {} [UUID: {}] to archive.", c.getId(), c.getUuid().getUuidString());
        archiver.addEntry(new ArchivableCommunication(c));
      }
    } catch (IOException e) {
      LOGGER.error("Caught IOException during output.", e);
    }
  }
}
