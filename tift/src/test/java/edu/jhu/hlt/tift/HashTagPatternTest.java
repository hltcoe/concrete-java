package edu.jhu.hlt.tift;

import static edu.jhu.hlt.tift.HashTagTagger.HASHTAG_PATTERN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;

import org.junit.Test;

public class HashTagPatternTest {

  final String sub = "#FelizDiaDeLaM";
  final String fake = "hola q tal " + sub;

  // Mutates m.
  private void checkPositiveMatch(Matcher m, String truth) {
    assertTrue(m.find());
    assertEquals(truth, m.group());
  }

  @Test
  public void positive() {
    final Matcher m = HASHTAG_PATTERN.matcher(fake);
    this.checkPositiveMatch(m, sub);
    assertFalse(m.find());
  }

  @Test
  public void positiveEllipsis() {
    final String subWEllipsis = sub + "...";
    final String fakeWEnd = "hola q tal " + subWEllipsis;

    final Matcher om = HASHTAG_PATTERN.matcher(fakeWEnd);
    this.checkPositiveMatch(om, sub);
    assertFalse(om.find());
  }

  @Test
  public void unicodeEllipse() {
    final String multi = "hello world #programming #pyth…";
    final Matcher tm = HASHTAG_PATTERN.matcher(multi);
    this.checkPositiveMatch(tm, "#programming");
    this.checkPositiveMatch(tm, "#pyth");
    assertFalse(tm.find());
  }

  @Test
  public void positiveMulti() {
    final String multi = "hello world #programming #pyth...";
    final Matcher tm = HASHTAG_PATTERN.matcher(multi);
    this.checkPositiveMatch(tm, "#programming");
    this.checkPositiveMatch(tm, "#pyth");
    assertFalse(tm.find());
  }

  @Test
  public void negative() {
    final String number = "quxbarfoo zzz31313 #1 bar";
    final String end = "quxbarfoo zzz31313 #1...";
    assertFalse(HASHTAG_PATTERN.matcher(number).find());
    assertFalse(HASHTAG_PATTERN.matcher(end).find());
  }
}
