package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.google.common.collect.ImmutableSet;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;

class TACKBP2017Opts {

  @Parameter(names = { "--help", "-h" }, help = true, description="Print usage information and exit.")
  public boolean help;

  @ParametersDelegate
  LDC2017E25Opts delegate2017 = new LDC2017E25Opts();

  @Parameter(names = {"--output-eng"},
      description = "Output file for English Communications")
  Path engOutput = Paths.get("eng.tar.gz");

  @Parameter(names = {"--output-zho"},
      description = "Output file for Chinese Communications")
  Path zhoOutput = Paths.get("zho.tar.gz");

  @Parameter(names = {"--output-spa"},
      description = "Output file for Spanish Communications")
  Path spaOutput = Paths.get("spa.tar.gz");

  static TarArchiver getArchiver(Path p) throws IOException {
    OutputStream os = Files.newOutputStream(p);
    BufferedOutputStream bout = new BufferedOutputStream(os);
    GzipCompressorOutputStream gout = new GzipCompressorOutputStream(bout);
    return new TarArchiver(gout);
  }

  TarArchiver spaArchiver() throws IOException {
    return getArchiver(spaOutput);
  }

  TarArchiver engArchiver() throws IOException {
    return getArchiver(engOutput);
  }

  TarArchiver zhoArchiver() throws IOException {
    return getArchiver(zhoOutput);
  }

  boolean validate() {
    Set<Path> paths = ImmutableSet.of(engOutput, zhoOutput, spaOutput);
    if (paths.size() != 3)
      return false;
    boolean nonExistent = true;
    Iterator<Path> piter = paths.iterator();
    while (piter.hasNext() && nonExistent) {
      nonExistent = !Files.exists(piter.next());
    }
    return nonExistent;
  }
}
