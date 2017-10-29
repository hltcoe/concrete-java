/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

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
    Path n = outputPath.toAbsolutePath();
    int nPaths = n.getNameCount();
    for (int i = 0; i < nPaths - 1; i++) {
      n = outputPath.getName(i);
      LOGGER.debug("On path: {}", n.toString());
      // is there another element left? if so, on a directory
      if (i + 1 < nPaths) {
        // does it exist?
        if (!Files.exists(n)) {
          // no - create intermediate directory
          LOGGER.info("Creating directory: {}", n.toString());
          Files.createDirectory(n);
        } else {
          // file exists - is it a directory?
          if (!Files.isDirectory(n)) {
            throw new IOException(outputPath.toString() + " exists and is not a directory");
          }
        }
      }
    }

    // now on the file itself, so exit iteration
    return Files.exists(n);
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
