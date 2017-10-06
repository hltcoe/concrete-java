package edu.jhu.hlt.concrete.ingesters.kbp2017.concrete;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

import edu.jhu.hlt.concrete.ingesters.base.PathConverter;

public class LDC2017E25Opts {

  private static final Logger LOGGER = LoggerFactory.getLogger(LDC2017E25Opts.class);

  static final String DISCUSSION_FORUM_FOLDER = "df";
  static final String NEWSWIRE_FOLDER = "nw";

  static final String ENGLISH = "eng";
  static final String SPANISH = "spa";
  static final String CHINESE = "cmn";

  static final String[] langs = new String[] { ENGLISH, SPANISH, CHINESE };
  static final String[] types = new String[] { DISCUSSION_FORUM_FOLDER, NEWSWIRE_FOLDER };

  @Parameter(names = {"--LDC2017E25-path"}, converter = PathConverter.class,
      description = "/path/to/LDC2017E25/data", required = true)
  public Path path;

  public boolean validate() {
    if (!Files.exists(this.path) || !Files.isDirectory(this.path))
      return false;
    LOGGER.debug("LDC2017E25 dir OK");
    for (String lps : langs) {
      LOGGER.debug("Checking {}", lps);
      Path lp = this.path.resolve(lps);
      LOGGER.debug("Checking path {}", lp.toString());
      if (!Files.exists(lp) || !Files.isDirectory(lp))
        return false;
      for (String tps : types) {
        LOGGER.debug("Checking type {}", tps);
        Path tp = lp.resolve(tps);
        if (!Files.exists(tp) || !Files.isDirectory(tp))
          return false;
      }
    }
    return true;
  }

  public Path english() {
    return this.path.resolve(ENGLISH);
  }

  public Path englishNW() {
    return this.english().resolve(NEWSWIRE_FOLDER);
  }

  public Path englishDF() {
    return this.english().resolve(DISCUSSION_FORUM_FOLDER);
  }

  public Path spanish() {
    return this.path.resolve(SPANISH);
  }

  public Path spanishNW() {
    return this.spanish().resolve(NEWSWIRE_FOLDER);
  }

  public Path spanishDF() {
    return this.spanish().resolve(DISCUSSION_FORUM_FOLDER);
  }

  public Path chinese() {
    return this.path.resolve(CHINESE);
  }

  public Path chineseNW() {
    return this.chinese().resolve(NEWSWIRE_FOLDER);
  }

  public Path chineseDF() {
    return this.chinese().resolve(DISCUSSION_FORUM_FOLDER);
  }
}
