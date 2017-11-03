/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.base;

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
import com.beust.jcommander.Parameter;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;

/**
 *
 */
public class IngesterParameterDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger(IngesterParameterDelegate.class);

  @Parameter(names="--output-path", description="The path to place output files.",
      converter = PathConverter.class)
  public Path outputPath = Paths.get("./comms.tar.gz");

  @Parameter(names = "--help", help = true, description="Print usage information and exit.")
  public boolean help;

  @Parameter(names = "--overwrite", description="Overwrite files?")
  public boolean overwrite = false;

  /**
   *
   */
  public IngesterParameterDelegate() {
    // TODO Auto-generated constructor stub
  }

  public TarArchiver getArchiver() throws IOException {
    OutputStream os = Files.newOutputStream(this.outputPath);
    BufferedOutputStream bout = new BufferedOutputStream(os);
    GzipCompressorOutputStream gout = new GzipCompressorOutputStream(bout);
    return new TarArchiver(gout);
  }

  public void prepare() throws IOException {
    boolean fileExists = IngesterParameterDelegate.prepare(this.outputPath);
    LOGGER.info("File already exists: {}", fileExists);
    if (fileExists) {
      if (!this.overwrite) {
        throw new IOException(this.outputPath.toString() + " exists and overwrite disabled");
      } else {
        Files.delete(this.outputPath);
      }
    }
  }

  public static boolean prepare(Path outputPath) throws IOException {
    LOGGER.debug("Passed path: {}", outputPath.toString());
    // want to extract out the last part from the directories
    final int nPaths = outputPath.getNameCount();
    LOGGER.debug("{} paths detected", nPaths);
    // if > 1 path, make sure directories exist
    if (nPaths > 1) {
      // get everything before last
      Path abs = outputPath.toAbsolutePath();
      LOGGER.debug("Abs: {}", abs.toString());
      Path folders = abs.getParent();
      LOGGER.debug("Parent: {}", folders.toString());
      // try to make the directories if needed
      LOGGER.debug("Optionally creating intermediate directories: {}", folders.toString());
      // is folders a symlink?
      while (Files.isSymbolicLink(folders)) {
        // update to its target
        folders = Files.readSymbolicLink(folders);
      }
      Files.createDirectories(folders);
    }

    // now on the file itself, so exit iteration
    return Files.exists(outputPath);
  }

  public static void main(String... args) {
    IngesterParameterDelegate run = new IngesterParameterDelegate();
    JCommander jc = JCommander.newBuilder().addObject(run).build();
    jc.parse(args);
    jc.setProgramName(IngesterParameterDelegate.class.getSimpleName());
    if (run.help) {
      jc.usage();
    }
  }
}
