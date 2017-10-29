package edu.jhu.hlt.concrete.ingesters.base;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.IStringConverter;

public class PathConverter implements IStringConverter<Path> {

  @Override
  public Path convert(String arg0) {
    return Paths.get(arg0);
  }
}
