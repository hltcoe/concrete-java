/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.webposts;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParametersDelegate;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.IngesterParameterDelegate;
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

  @ParametersDelegate
  private IngesterParameterDelegate delegate = new IngesterParameterDelegate();

  /**
   * @param args
   */
  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    WebPostIngesterRunner run = new WebPostIngesterRunner();
    JCommander jc = JCommander.newBuilder().addCommand(run).build();
    jc.parse(args);
    jc.setProgramName(WebPostIngesterRunner.class.getSimpleName());
    if (run.delegate.help) {
      jc.usage();
    }

    try {
      Path outpath = Paths.get(run.delegate.outputPath);
      IngesterParameterDelegate.prepare(outpath);
      WebPostIngester ing = new WebPostIngester();
      Path outWithExt = outpath.resolve("webposts.tar.gz");

      if (Files.exists(outWithExt)) {
        if (!run.delegate.overwrite) {
          LOGGER.info("File: {} exists and overwrite disabled. Not running.", outWithExt.toString());
          return;
        } else {
          Files.delete(outWithExt);
        }
      }

      try (OutputStream os = Files.newOutputStream(outWithExt);
          GzipCompressorOutputStream gout = new GzipCompressorOutputStream(os);
          TarArchiver arch = new TarArchiver(gout)) {
        for (String pstr : run.delegate.paths) {
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
