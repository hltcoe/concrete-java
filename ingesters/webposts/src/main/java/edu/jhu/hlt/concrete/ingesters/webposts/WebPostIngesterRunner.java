/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.webposts;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Class used for bulk conversion of web post documents found in various LDC
 * corpora.
 *
 * @see #main(String...)
 */
public class WebPostIngesterRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebPostIngesterRunner.class);

  /**
   * @param args
   */
  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    Opts run = new Opts();
    JCommander jc = JCommander.newBuilder().addCommand(run).build();
    jc.parse(args);
    jc.setProgramName(WebPostIngesterRunner.class.getSimpleName());
    if (run.delegate.help) {
      jc.usage();
    }

    try {
      run.delegate.prepare();
      Path outpath = run.delegate.outputPath;
      WebPostIngester ing = new WebPostIngester();

      try (OutputStream os = Files.newOutputStream(outpath);
          BufferedOutputStream bout = new BufferedOutputStream(os);
          GzipCompressorOutputStream gout = new GzipCompressorOutputStream(bout);
          TarArchiver arch = new TarArchiver(gout)) {
        for (String pstr : run.paths) {
          LOGGER.debug("Running on file: {}", pstr);
          Path p = Paths.get(pstr);
          new ExistingNonDirectoryFile(p);
          try {
            Communication next = ing.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + pstr, e);
          }
        }
      }
    } catch (NotFileException | IOException e) {
      LOGGER.error("Caught exception processing.", e);
    }
  }
}
