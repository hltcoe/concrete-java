/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 *
 */
public class IngesterParameterDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger(IngesterParameterDelegate.class);

  @Parameter(required=true, names="--output-path", description="The path to place output files.")
  public String outputPath;

  @Parameter(names = "--help", help = true, description="Print usage information and exit.")
  public boolean help;

  @Parameter(names = "--overwrite", description="Overwrite files?")
  public boolean overwrite = false;

  @Parameter(description = "path/to/file1 /path/to/file2/ ...")
  public List<String> paths = new ArrayList<>();

  /**
   *
   */
  public IngesterParameterDelegate() {
    // TODO Auto-generated constructor stub
  }

  public static void prepare(Path outputPath) throws IOException {
    // Does not exist - create.
    if (!Files.exists(outputPath)) {
      LOGGER.debug("Attempting to create output directory.");
      Files.createDirectories(outputPath);
    } else {
      // Exists but is not directory - stop.
      if (!Files.isDirectory(outputPath)) {
        LOGGER.error("{} exists and is not a directory. Not continuing.");
        return;
      }
    }
  }

  public static void main(String... args) {
    IngesterParameterDelegate run = new IngesterParameterDelegate();
    JCommander jc = new JCommander(run, args);
    jc.setProgramName(IngesterParameterDelegate.class.getSimpleName());
    if (run.help) {
      jc.usage();
    }
  }
}
