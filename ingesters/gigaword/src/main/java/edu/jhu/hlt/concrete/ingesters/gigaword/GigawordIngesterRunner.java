package edu.jhu.hlt.concrete.ingesters.gigaword;

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
import edu.jhu.hlt.concrete.ingesters.base.IngesterOpts;
import edu.jhu.hlt.concrete.ingesters.base.IngesterParameterDelegate;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

public class GigawordIngesterRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(GigawordIngesterRunner.class);

  /**
   * @param args
   */
  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    IngesterOpts run = new IngesterOpts();
    JCommander jc = JCommander.newBuilder().addObject(run).build();
    jc.parse(args);
    jc.setProgramName(GigawordIngesterRunner.class.getSimpleName());
    if (run.delegate.help) {
      jc.usage();
    }

    try {
      Path outpath = Paths.get(run.delegate.outputPath);
      IngesterParameterDelegate.prepare(outpath);

      GigawordDocumentConverter conv = new GigawordDocumentConverter();
      for (String pstr : run.paths) {
        LOGGER.debug("Running on file: {}", pstr);
        Path p = Paths.get(pstr);
        new ExistingNonDirectoryFile(p);
        Path outWithExt = outpath.resolve(p.getFileName().toString().split("\\.")[0] + ".tar.gz");

        if (Files.exists(outWithExt)) {
          if (!run.delegate.overwrite) {
            LOGGER.info("File: {} exists and overwrite disabled. Not running.", outWithExt.toString());
            continue;
          } else {
            Files.delete(outWithExt);
          }
        }

        try(OutputStream os = Files.newOutputStream(outWithExt);
            GzipCompressorOutputStream gout = new GzipCompressorOutputStream(os);
            TarArchiver arch = new TarArchiver(gout)) {
          Iterator<Communication> iter = conv.gzToStringIterator(p);
          while (iter.hasNext()) {
            arch.addEntry(new ArchivableCommunication(iter.next()));
          }
        }
      }
    } catch (NotFileException | IOException e) {
      LOGGER.error("Caught exception processing.", e);
    }
  }
}
