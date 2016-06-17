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
import java.util.stream.Collectors;

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
        .filter(tl -> tl.getTaggingType().equalsIgnoreCase("twitter"))
        .findFirst();
    assertTrue(tt.isPresent());
    for (TaggedToken t : tt.get().getTaggedTokenList()) {
      int idx = t.getTokenIndex();
      logger.info("Got tagging: {} on token: {}", t.getTag(), idx);
      assertEquals(4, idx);
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

  @Test
  public void testEndURL() {
    final String test = "'La traición vendrá de un general de alto rango que generará un gran caos' - http://t.co/MgLypirfTV http://…";
    Tokenization t = Tokenizer.TWITTER.tokenizeToConcrete(test);
    assertTrue(t.isSetTokenTaggingList());
    List<TokenTagging> ttl = t.getTokenTaggingList();
    assertEquals(1, ttl.size());
    TokenTagging tt = ttl.get(0);
    assertEquals("twitter", tt.getTaggingType());
    List<TaggedToken> tagTL = tt.getTaggedTokenList().stream()
        .filter(tagtok -> tagtok.getTag().equals("URL"))
        .collect(Collectors.toList());
    logger.debug("Tags:");
    tagTL.stream()
        .map(TaggedToken::getTag)
        .forEach(logger::debug);
    assertEquals(2, tagTL.size());
    TaggedToken last = tagTL.get(tagTL.size() - 1);
    assertTrue(t.isSetTokenList());
    List<Token> tl = t.getTokenList().getTokenList();
    logger.debug("tokens:");
    tl.stream()
      .map(Token::getText)
      .forEach(logger::debug);
    assertEquals("Should get 'http://' as text for last token.", "http://…", tl.get(last.getTokenIndex()).getText());
    assertEquals("Type of last token should be 'URL'.", "URL", last.getTag());
  }

  static String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return encoding.decode(ByteBuffer.wrap(encoded)).toString();
  }
}
