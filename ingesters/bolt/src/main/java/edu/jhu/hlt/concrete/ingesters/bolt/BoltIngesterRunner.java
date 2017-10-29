/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.bolt;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.IngesterOpts;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Class used for bulk conversion of the BOLT corpus.
 *
 * @see #main(String...)
 */
public class BoltIngesterRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(BoltIngesterRunner.class);

  /**
   *
   */
  public BoltIngesterRunner() {

  }

  /**
   * @param args
   */
  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    IngesterOpts run = new IngesterOpts();
    JCommander jc = JCommander.newBuilder().addObject(run).build();
    jc.parse(args);
    jc.setProgramName(BoltIngesterRunner.class.getSimpleName());
    if (run.delegate.help) {
      jc.usage();
      return;
    }

    try {
      run.delegate.prepare();
      Path outpath = run.delegate.outputPath;
      BoltForumPostIngester ing = new BoltForumPostIngester();

      try (OutputStream os = Files.newOutputStream(outpath);
          BufferedOutputStream bout = new BufferedOutputStream(os);
          GzipCompressorOutputStream gout = new GzipCompressorOutputStream(bout);
          TarArchiver arch = new TarArchiver(gout)) {
        List<Path> paths = run.findFilesInPaths();
        for (Path p : paths) {
          LOGGER.debug("Running on file: {}", p);
          new ExistingNonDirectoryFile(p);
          try {
            Communication next = ing.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + p, e);
          }
        }

        LOGGER.info("Ingested {} discussion forum posts", paths.size());
      }
    } catch (NotFileException | IOException e) {
      LOGGER.error("Caught exception processing.", e);
    }
  }
}
