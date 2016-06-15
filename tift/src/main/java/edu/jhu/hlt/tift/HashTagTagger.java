/*
 *
 */
package edu.jhu.hlt.tift;

import java.util.regex.Pattern;

/**
 *
 */
class HashTagTagger {
  public static final Pattern HASHTAG_PATTERN = Pattern.compile("\\B#\\w*[a-zA-Z]+\\w*");
}
