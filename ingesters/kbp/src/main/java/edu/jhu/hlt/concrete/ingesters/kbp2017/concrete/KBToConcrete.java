package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngesterParameterDelegate;
import edu.jhu.hlt.concrete.ingesters.kbp2017.KBParameterDelegate;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.concrete.serialization.iterators.TarGzArchiveEntryCommunicationIterator;

public class KBToConcrete {

  private static final Logger LOGGER = LoggerFactory.getLogger(KBToConcrete.class);

  private static class Opts {
    @Parameter(description = "Path to the communications .tar.gz file",
        names = {"--comms-path", "-in"}, required = true)
    String pathToCommsTarGZ;

    @ParametersDelegate
    KBParameterDelegate kbParams = new KBParameterDelegate();

    @ParametersDelegate
    IngesterParameterDelegate ingesterParams = new IngesterParameterDelegate();
  }

  public static void main(String... args) {
    Opts o = new Opts();
    JCommander jc  = JCommander.newBuilder().addObject(o).build();
    jc.parse(args);
    if (o.ingesterParams.help) {
      jc.usage();
      return;
    }

    Path kbPath = o.kbParams.kbPath;
    if (!Files.exists(kbPath)) {
      System.out.println("Input file does not exist");
      System.exit(1);
    }

    Path commsPath = Paths.get(o.pathToCommsTarGZ);
    if (!Files.exists(commsPath)) {
      System.out.println("Comms .tar.gz file does not exist");
      System.exit(1);
    }

    try {
      o.ingesterParams.prepare();
    } catch (IOException e) {
      System.out.println("Failed preparation step: " + e.getMessage());
      System.exit(2);
    }

    boolean failed = false;
    try (InputStream in = Files.newInputStream(kbPath);
        BufferedInputStream bin = new BufferedInputStream(in, 1024 * 32);
        GzipCompressorInputStream gin = new GzipCompressorInputStream(bin);) {
      ConcreteSubmittedKB ckb = new ConcreteSubmittedKB(gin);
      LOGGER.info("Finished KB load");
      try (InputStream cin = Files.newInputStream(commsPath);
          BufferedInputStream cbin = new BufferedInputStream(cin, 1024 * 32);
          TarGzArchiveEntryCommunicationIterator iter = new TarGzArchiveEntryCommunicationIterator(cbin);) {
        try (TarArchiver arch = o.ingesterParams.getArchiver();) {
          LOGGER.info("Beginning processing");
          int nProcessed = 0;
          while (iter.hasNext()) {
            Communication next = iter.next();
            Communication annotated = ckb.process(next);
            arch.addEntry(new ArchivableCommunication(annotated));
            nProcessed++;

            if (nProcessed%1000 == 0)
              LOGGER.info("Checkpoint: processed {} comms", nProcessed);
          }
          LOGGER.info("Processed {} comms", nProcessed);
        }
      }
    } catch (IOException e) {
      LOGGER.error("Exception parsing file", e);
      failed = true;
    }

    if (failed)
      System.exit(128);
  }
}
