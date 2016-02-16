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

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
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
 *
 */
public class ALNCIngesterRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(ALNCIngesterRunner.class);

  @ParametersDelegate
  private IngesterParameterDelegate delegate = new IngesterParameterDelegate();

  /**
   *
   */
  public ALNCIngesterRunner() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param args
   */
  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    ALNCIngesterRunner run = new ALNCIngesterRunner();
    JCommander jc = new JCommander(run, args);
    jc.setProgramName(ALNCIngesterRunner.class.getSimpleName());
    if (run.delegate.help) {
      jc.usage();
    }

    try {
      Path outpath = Paths.get(run.delegate.outputPath);
      IngesterParameterDelegate.prepare(outpath);

      for (String pstr : run.delegate.paths) {
        Path p = Paths.get(pstr);
        new ExistingNonDirectoryFile(p);
        try (ALNCIngester ing = new ALNCIngester(p);
            OutputStream os = Files.newOutputStream(outpath.resolve(p.getFileName() + ".gz"));
            BZip2CompressorOutputStream gout = new BZip2CompressorOutputStream(os);
            TarArchiver arch = new TarArchiver(gout)) {
          Iterator<Communication> iter = ing.iterator();
          while (iter.hasNext()) {
            new ArchivableCommunication(iter.next());
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
