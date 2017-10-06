package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.IngesterOpts;
import edu.jhu.hlt.concrete.ingesters.bolt.BoltForumPostIngester;
import edu.jhu.hlt.concrete.ingesters.webposts.TACKBP2017NewsWireIngester;
import edu.jhu.hlt.concrete.ingesters.webposts.TACKBP2017WebPostIngester;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

public class TACKBP2017DocumentIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(TACKBP2017DocumentIngester.class);

  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    TACKBP2017Opts run = new TACKBP2017Opts();
    JCommander jc = JCommander.newBuilder().addObject(run).build();
    jc.parse(args);
    jc.setProgramName(TACKBP2017DocumentIngester.class.getSimpleName());
    if (run.help) {
      jc.usage();
      return;
    }

    try {
      if (!run.delegate2017.validate()) {
        System.out.println("Input is not a LDC2017E25/data directory");
        System.exit(2);
      }
      LOGGER.info("LDC2017E25 path is OK");

      if (!run.validate()) {
        System.out.println("Invalid parameters. Either the files already exist or some were duplicates.");
        System.exit(4);
      }
      LOGGER.info("Output paths OK");

      // this is for NYT_ docs
      TACKBP2017NewsWireIngester ing = new TACKBP2017NewsWireIngester();
      // TODO this needs fixing
      // this is for other newswire docs
      TACKBP2017WebPostIngester otherING = new TACKBP2017WebPostIngester();
      // discussion forum ingester
      BoltForumPostIngester dfIngester = new BoltForumPostIngester();

      // for ENGLISH, want to find anything in
      // LDC2015E77 and the eng dir in 2017E25
      try (TarArchiver arch = run.engArchiver();) {
        // start with newswire
        Path nw2017Eng = run.delegate2017.englishNW();
        LOGGER.info("Running over newswire: {}", nw2017Eng.toString());
        for (Path p : IngesterOpts.findFiles(nw2017Eng)) {
          LOGGER.debug("Running on file: {}", p.toString());
          String fn = new ExistingNonDirectoryFile(p).getName();
          try {
            Communication next;
            if (fn.startsWith("NYT_"))
              next = ing.fromCharacterBasedFile(p);
            else
              next = otherING.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + p.toString(), e);
          }
        }

        // now do all english DF
        Path dfp = run.delegate2017.englishDF();
        LOGGER.info("Running over discussion forum posts: {}", dfp.toString());
        for (Path p : IngesterOpts.findFiles(dfp)) {
          new ExistingNonDirectoryFile(p);
          try {
            Communication next = dfIngester.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + p.toString(), e);
          }
        }
      }
      // now chinese NW + DF
      try (TarArchiver arch = run.zhoArchiver();) {
        Path nwp = run.delegate2017.chineseNW();
        LOGGER.info("Running over newswire: {}", nwp.toString());
        for (Path p : IngesterOpts.findFiles(nwp)) {
          try {
            Communication next = otherING.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + p.toString(), e);
          }
        }

        Path dfp = run.delegate2017.chineseDF();
        LOGGER.info("Running over discussion forum posts: {}", dfp.toString());
        for (Path p : IngesterOpts.findFiles(dfp)) {
          new ExistingNonDirectoryFile(p);
          try {
            Communication next = dfIngester.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + p.toString(), e);
          }
        }
      }
      // now spanish NW + DF
      try (TarArchiver arch = run.spaArchiver();) {
        Path nwp = run.delegate2017.spanishNW();
        LOGGER.info("Running over newswire: {}", nwp.toString());
        for (Path p : IngesterOpts.findFiles(nwp)) {
          try {
            Communication next = otherING.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + p.toString(), e);
          }
        }

        Path dfp = run.delegate2017.spanishDF();
        LOGGER.info("Running over discussion forum posts: {}", dfp.toString());
        for (Path p : IngesterOpts.findFiles(dfp)) {
          new ExistingNonDirectoryFile(p);
          try {
            Communication next = dfIngester.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + p.toString(), e);
          }
        }
      }

    } catch (NotFileException | IOException e) {
      LOGGER.error("Caught exception processing.", e);
    }
  }
}
