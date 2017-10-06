package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.nio.file.Files;
import java.nio.file.Path;

import com.beust.jcommander.Parameter;

import edu.jhu.hlt.concrete.ingesters.base.PathConverter;

public class LDC2015E77Opts {
  @Parameter(names = {"--LDC2015E77-path"}, converter = PathConverter.class,
      description = "/path/to/LDC2015E77/data/lang", required = true)
  public Path path;

  public boolean validate() {
    if (!Files.exists(this.path) || !Files.isDirectory(this.path))
      return false;
    Path df = this.getDiscussionForumPath();
    if (!Files.exists(df) || !Files.isDirectory(df))
      return false;
    Path nw = this.getNewswirePath();
    if (!Files.exists(nw) || !Files.isDirectory(nw))
      return false;
    return true;
  }

  public Path getDiscussionForumPath() {
    return this.path.resolve("mpdf");
  }

  public Path getNewswirePath() {
    return this.path.resolve("nw");
  }
}
