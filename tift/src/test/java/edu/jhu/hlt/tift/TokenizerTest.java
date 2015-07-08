package edu.jhu.hlt.tift;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.TaggedToken;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.serialization.BoundedThriftSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

public class TokenizerTest {

  private static final Logger logger = LoggerFactory.getLogger(TokenizerTest.class);

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testTokenizeToConcreteWhitespace() {
    String text = "hello world test tokens foo";
    int expectedTokenCount = 5;
    Tokenization ct = Tokenizer.WHITESPACE.tokenizeToConcrete(text, 0);
    List<Token> tokenList = ct.getTokenList().getTokenList();
    assertEquals(expectedTokenCount, tokenList.size());
    for (Token t : tokenList) {
      logger.info("Got token: {} with text: {}", t.getTokenIndex(), t.getText());
      TextSpan ts = t.getTextSpan();
      logger.info("Text span of this token: {} - {}", ts.getStart(), ts.getEnding());
    }
  }

  @Test
  public void testTokenizeToConcreteTwitter() {
    String text = "hello world test foo :-)";
    int expectedTokenCount = 5;
    Tokenization ct = Tokenizer.TWITTER.tokenizeToConcrete(text, 0);
    List<Token> tokenList = ct.getTokenList().getTokenList();
    assertEquals(expectedTokenCount, tokenList.size());
    for (Token t : tokenList) {
      logger.info("Got token: {} with text: {}", t.getTokenIndex(), t.getText());
      TextSpan ts = t.getTextSpan();
      logger.info("Text span of this token: {} - {}", ts.getStart(), ts.getEnding());
    }

    Optional<TokenTagging> tt = ct.getTokenTaggingList()
        .stream()
        .filter(tl -> tl.getTaggingType().equalsIgnoreCase("POS"))
        .findFirst();
    assertTrue(tt.isPresent());
    for (TaggedToken t : tt.get().getTaggedTokenList()) {
      logger.info("Got tagging: {} on token: {}", t.getTag(), t.getTokenIndex());
    }
  }

  @Test
  public void testTokenize() {
    String text = "hello world test tokens";
    List<String> tokens = Tokenizer.BASIC.tokenize(text);
    assertEquals(4, tokens.size());
  }
  
  @Test
  public void thriftReadWrite() throws ConcreteException {
    String text = "hello world test tokens";
    Tokenization t = Tokenizer.TWITTER.tokenizeToConcrete(text, 0);
    BoundedThriftSerializer<Tokenization> ser = new BoundedThriftSerializer<>(Tokenization.class);
    byte[] bytez = ser.toBytes(t);
    Tokenization dT = ser.fromBytes(bytez);
    assertEquals(4, dT.getTokenList().getTokenListSize());
  }

  static String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return encoding.decode(ByteBuffer.wrap(encoded)).toString();
  }
}
