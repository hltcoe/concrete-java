/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 *
 */
public class IngesterParameterDelegate {

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
    if (fileExists && !this.overwrite) {
      System.out.println(this.outputPath.toString() + " exists and overwrite disabled");
      System.exit(2);
    } else {
      Files.delete(this.outputPath);
    }
  }

  public static boolean prepare(Path outputPath) throws IOException {
    Iterator<Path> paths = outputPath.iterator();
    Path n = outputPath;
    while (paths.hasNext()) {
      n = paths.next();
      // is there another element left? if so, on a directory
      if (paths.hasNext()) {
        // does it exist?
        if (!Files.exists(n)) {
          // no - create intermediate directory
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
    return !Files.exists(n);
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
