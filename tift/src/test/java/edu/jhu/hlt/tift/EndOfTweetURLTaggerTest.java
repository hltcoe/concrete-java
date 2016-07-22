package edu.jhu.hlt.tift;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;

import org.junit.Test;

public class EndOfTweetURLTaggerTest {

  final String test = "'La traición vendrá de un general de alto rango que generará un gran caos' - http://t.co/MgLypirfTV http";

  @Test
  public void endOfURL() {
    Matcher m = EndOfTweetURLTagger.END_URL.matcher(test);
    assertTrue("Should find a match.", m.find());
    assertEquals("http", m.group().trim());
  }
}
