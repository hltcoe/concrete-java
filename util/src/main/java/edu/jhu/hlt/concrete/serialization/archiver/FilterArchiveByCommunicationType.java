/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization.archiver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.iterators.TarGzArchiveEntryCommunicationIterator;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;

/**
 * Tiny utility that takes in an archive of Concrete {@link Communication}
 * objects and drops ones of the specified types.
 */
public class FilterArchiveByCommunicationType {

  private static final Logger LOGGER = LoggerFactory.getLogger(FilterArchiveByCommunicationType.class);

  @Parameter
  private Set<String> paramList = new HashSet<>();

  @Parameter(names = "--help", help=true, description = "Print the usage information and exit.")
  private boolean help;

  @Parameter(names = "--types", required=true, description = "A list of strings that represent types to drop from the archive.")
  private List<String> typeList = new ArrayList<>();

  @Parameter(names = "--input-file", required=true, description = "The input file to filter.")
  private String inFile;

  @Parameter(names = "--output-file", required=true, description = "The file where output will be stored.")
  private String outFile;

  /**
   * @param args
   */
  public static void main(String[] args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    FilterArchiveByCommunicationType m = new FilterArchiveByCommunicationType();
    JCommander jc = new JCommander(m, args);
    jc.setProgramName(FilterArchiveByCommunicationType.class.getName());
    if (m.help) {
      jc.usage();
      return;
    }

    Predicate<Communication> notOfTypePred = comm -> !m.paramList.contains(comm.getType());
    try {
      ExistingNonDirectoryFile ef = new ExistingNonDirectoryFile(Paths.get(m.inFile));
      Path p = ef.getPath();
      try (InputStream is = Files.newInputStream(p);
          BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 24);

          OutputStream os = Files.newOutputStream(Paths.get(m.outFile));
          GzipCompressorOutputStream gout = new GzipCompressorOutputStream(os);
          BufferedOutputStream bos = new BufferedOutputStream(gout, 1024 * 8 * 24);
          TarArchiver arch = new TarArchiver(bos);) {
        TarGzArchiveEntryCommunicationIterator iter = new TarGzArchiveEntryCommunicationIterator(bin);
        final Iterable<Communication> ic = () -> iter;
        StreamSupport.stream(ic.spliterator(), false)
            .filter(notOfTypePred)
            .forEach(c -> {
              try {
                arch.addEntry(new ArchivableCommunication(c));
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            });
      }

      LOGGER.info("Finished with archive: {}", m.inFile);
    } catch (Exception e) {
      LOGGER.error("Caught exception filtering over archive: " + m.inFile, e);
    }
  }
}
