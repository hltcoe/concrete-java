package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.beust.jcommander.Parameter;

import edu.jhu.hlt.concrete.ingesters.base.PathConverter;

public class KBParameterDelegate {
  @Parameter(description = "Path to the KB File",
      converter = PathConverter.class,
      names = {"--kb-path", "-kb"}, required = true)
  public Path kbPath;

  public BufferedReader getReader() throws IOException {
    InputStream in = Files.newInputStream(this.kbPath);
    BufferedInputStream bin = new BufferedInputStream(in, 1024 * 32);
    GzipCompressorInputStream gin = new GzipCompressorInputStream(bin);
    InputStreamReader irdr = new InputStreamReader(gin, StandardCharsets.UTF_8);
    return new BufferedReader(irdr);
  }
}
