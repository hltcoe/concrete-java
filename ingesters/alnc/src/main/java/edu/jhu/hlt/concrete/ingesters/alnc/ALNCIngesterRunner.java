/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.alnc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.IngesterOpts;
import edu.jhu.hlt.concrete.ingesters.base.IngesterParameterDelegate;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Class used for bulk conversion of the ALNC corpus.
 *
 * @see #main(String...)
 */
public class ALNCIngesterRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(ALNCIngesterRunner.class);

  /**
   *
   */
  public ALNCIngesterRunner() {

  }

  /**
   * @param args
   */
  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    IngesterOpts run = new IngesterOpts();
    JCommander jc = JCommander.newBuilder().addObject(run).build();
    jc.parse(args);
    jc.setProgramName(ALNCIngesterRunner.class.getSimpleName());
    if (run.delegate.help) {
      jc.usage();
    }

    try {
      Path outpath = Paths.get(run.delegate.outputPath);
      IngesterParameterDelegate.prepare(outpath);

      for (String pstr : run.paths) {
        LOGGER.debug("Running on file: {}", pstr);
        Path p = Paths.get(pstr);
        new ExistingNonDirectoryFile(p);
        Path outWithExt = outpath.resolve(p.getFileName() + ".tar.gz");

        if (Files.exists(outWithExt)) {
          if (!run.delegate.overwrite) {
            LOGGER.info("File: {} exists and overwrite disabled. Not running.", outWithExt.toString());
            continue;
          } else {
            Files.delete(outWithExt);
          }
        }

        try (ALNCIngester ing = new ALNCIngester(p);
            OutputStream os = Files.newOutputStream(outWithExt);
            GzipCompressorOutputStream gout = new GzipCompressorOutputStream(os);
            TarArchiver arch = new TarArchiver(gout)) {
          Iterator<Communication> iter = ing.iterator();
          while (iter.hasNext()) {
            Communication c = iter.next();
            LOGGER.debug("Got comm: {}", c.getId());
            arch.addEntry(new ArchivableCommunication(c));
          }
        } catch (IngestException e) {
          LOGGER.error("Caught exception processing path: " + pstr, e);
        }
      }
    } catch (NotFileException | IOException e) {
      LOGGER.error("Caught exception processing.", e);
    }
  }
}
