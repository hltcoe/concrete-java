package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import edu.jhu.hlt.concrete.ingesters.base.IngesterParameterDelegate;

class TACKBP2017Opts {

  @ParametersDelegate
  public IngesterParameterDelegate delegate = new IngesterParameterDelegate();

  @Parameter(description = "/path/to/LDC2017E25/data/lang", required = true)
  public List<String> paths = new ArrayList<>();

  public boolean validate() {
    if (paths.isEmpty())
      return false;
    Path root = this.toPath();
    if (!Files.exists(root) || !Files.isDirectory(root))
      return false;
    Path df = this.getDiscussionForumPath();
    if (!Files.exists(df) || !Files.isDirectory(df))
      return false;
    Path nw = this.getNewswirePath();
    if (!Files.exists(nw) || !Files.isDirectory(nw))
      return false;
    return true;
  }

  public Path toPath() {
    return Paths.get(paths.get(0));
  }

  public Path getDiscussionForumPath() {
    return this.toPath().resolve("df");
  }

  public Path getNewswirePath() {
    return this.toPath().resolve("nw");
  }
}
