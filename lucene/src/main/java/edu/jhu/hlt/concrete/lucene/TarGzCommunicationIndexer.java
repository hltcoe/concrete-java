package edu.jhu.hlt.concrete.lucene;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.iterators.TarGzArchiveEntryCommunicationIterator;
import edu.jhu.hlt.utilt.AutoCloseableIterator;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.NotFileException;

public class TarGzCommunicationIndexer {

  private static final Logger LOGGER = LoggerFactory.getLogger(TarGzCommunicationIndexer.class);

  private static class Opts {
    @Parameter(names = { "--help", "-h" },
        help = true, description = "Print the usage information and exit.")
    boolean help;

    @Parameter(names = "--input-path", required = true,
        description = "Path to .tar.gz file to create an index over.")
    String inPathStr;

    @Parameter(names = "--output-folder", required = true,
        description = "Path to folder to store the index.")
    String outPathStr;

    public LuceneCommunicationIndexer getIndexer() throws NoSuchFileException, IOException, NotFileException {
      Path p = Paths.get(outPathStr);
      if (Files.exists(p)) {
        if (!Files.isDirectory(p))
          throw new IOException("Output path exists and is not a directory.");
      } else
        Files.createDirectories(p);
      return new NaiveConcreteLuceneIndexer(p);
    }

    public AutoCloseableIterator<Communication> getInputStream() throws IOException {
      return new TarGzArchiveEntryCommunicationIterator(new BufferedInputStream(Files.newInputStream(Paths.get(inPathStr))));
    }
  }

  public static void main (String ... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    Opts o = new Opts();
    JCommander jc = new JCommander(o, args);
    jc.setProgramName(TarGzCommunicationIndexer.class.getName());
    if (o.help) {
      jc.usage();
      return;
    }

    try (AutoCloseableIterator<Communication> inputComms = o.getInputStream();
        LuceneCommunicationIndexer idxer = o.getIndexer();) {
      LOGGER.info("Beginning.");
      while (inputComms.hasNext())
        idxer.add(inputComms.next());
    } catch (IOException e) {
      LOGGER.error("Caught IOException.", e);
    } catch (Exception e) {
      LOGGER.info("Caught Exception closing stream.", e);
    }

    LOGGER.info("Finished.");
  }
}
