package edu.jhu.hlt.tift;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class EndOfTweetHashtagsTest {

  @Test
  public void endOfTweetHashtagNotSplit() {
    final String target = "#FelizDiaDeLaM";
    List<String> tokens = Tokenizer.TWITTER.tokenize("hola q tal #FelizDiaDeLaM...");
    String last = tokens.get(tokens.size() - 2);
    assertEquals("Expected " + target + " but did not get it.", target, last);
  }

  @Test
  public void middleHashtag() {
    final String target = "hello world #Foo bar";
    List<String> tokens = Tokenizer.TWITTER.tokenize(target);
    String secondToLast = tokens.get(tokens.size() - 2);
    assertEquals("Expected " + "#Foo" + " but did not get it.", "#Foo", secondToLast);
  }

  @Test
  public void startHashtag() {
    final String target = "#hello world Foo bar";
    List<String> tokens = Tokenizer.TWITTER.tokenize(target);
    String secondToLast = tokens.get(0);
    assertEquals("Expected " + "#hello" + " but did not get it.", "#hello", secondToLast);
  }

  @Test
  public void tagWEllipses() {
    final String target = "hello world #Foo bar......";
    List<String> tokens = Tokenizer.TWITTER.tokenize(target);
    String secondToLast = tokens.get(tokens.size() - 3);
    assertEquals("Expected " + "#Foo" + " but did not get it.", "#Foo", secondToLast);
  }
}
