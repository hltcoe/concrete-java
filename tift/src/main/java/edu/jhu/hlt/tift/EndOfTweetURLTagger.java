/*
 *
 */
package edu.jhu.hlt.tift;

import java.util.regex.Pattern;

/**
 *
 */
class EndOfTweetURLTagger {
  public static final Pattern END_URL = Pattern.compile(" (https?[^ ]*?)$");
}
