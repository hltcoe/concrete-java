/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.annotatednyt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.nytlabs.corpus.NYTCorpusDocumentParser;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.acute.iterators.tar.TarGzArchiveEntryByteIterator;
import edu.jhu.hlt.annotatednyt.AnnotatedNYTDocument;
import edu.jhu.hlt.concrete.ingesters.base.IngesterOpts;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Class used for bulk conversion of the Annotated NYT corpus.
 *
 * @see #main(String...)
 */
public class AnnotatedNYTIngesterRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedNYTIngesterRunner.class);

  /**
   * @param args
   */
  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    IngesterOpts run = new IngesterOpts();
    JCommander jc = JCommander.newBuilder().addObject(run).build();
    jc.parse(args);
    jc.setProgramName(AnnotatedNYTIngesterRunner.class.getSimpleName());
    if (run.delegate.help) {
      jc.usage();
    }

    try {
      run.delegate.prepare();
      Path outpath = run.delegate.outputPath;

      NYTCorpusDocumentParser parser = new NYTCorpusDocumentParser();
      for (String pstr : run.paths) {
        LOGGER.debug("Running on file: {}", pstr);
        Path p = Paths.get(pstr);
        new ExistingNonDirectoryFile(p);
        int nPaths = p.getNameCount();
        Path year = p.getName(nPaths - 2);
        Path outWithExt = outpath.resolve(year.toString() + p.getFileName());

        if (Files.exists(outWithExt)) {
          if (!run.delegate.overwrite) {
            LOGGER.info("File: {} exists and overwrite disabled. Not running.", outWithExt.toString());
            continue;
          } else {
            Files.delete(outWithExt);
          }
        }

        try(InputStream is = Files.newInputStream(p);
            BufferedInputStream bin = new BufferedInputStream(is);
            TarGzArchiveEntryByteIterator iter = new TarGzArchiveEntryByteIterator(bin);

            OutputStream os = Files.newOutputStream(outWithExt);
            GzipCompressorOutputStream gout = new GzipCompressorOutputStream(os);
            TarArchiver arch = new TarArchiver(gout)) {
          Iterable<byte[]> able = () -> iter;
          StreamSupport.stream(able.spliterator(), false)
              .map(ba -> parser.fromByteArray(ba, false))
              .map(doc -> new AnnotatedNYTDocument(doc))
              .map(and -> new CommunicationizableAnnotatedNYTDocument(and).toCommunication())
              .forEach(comm -> {
                try {
                  arch.addEntry(new ArchivableCommunication(comm));
                } catch (IOException e) {
                  LOGGER.error("Caught exception processing file: " + pstr, e);
                }
              });
        }
      }
    } catch (NotFileException | IOException e) {
      LOGGER.error("Caught exception processing.", e);
    }
  }
}
