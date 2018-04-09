package edu.jhu.hlt.concrete.communications;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenList;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * Class that generates an example {@link Communication}.
 * <br><br>
 * Useful as both a testing tool and as an example for creating
 * Concrete {@link Communication} objects.
 * <br><br>
 * Especially useful to both ingester and analytic developers is the
 * code that generates UUID objects for the various Concrete objects.
 * This example should be followed by developers to generate compressible
 * UUIDs, that are both unique enough to be useful and compress well
 * in text format.
 */
public class ExampleCommunication {

  private final AnalyticUUIDGeneratorFactory f;
  private final AnalyticUUIDGenerator g;

  private static final String hello = "hello";
  private static final String world = "world";
  private static final String text = hello + " " + world;

  public ExampleCommunication() {
    this.f = new AnalyticUUIDGeneratorFactory();
    this.g = this.f.create();
  }
  /**
   *
   * @return an {@link AnnotationMetadata} object with required fields set
   */
  public static AnnotationMetadata annotationMetadata() {
    AnnotationMetadata md = new AnnotationMetadata();
    md.setTool("ExampleCommunication tool");
    md.setTimestamp(Timing.currentUTCTime());
    return md;
  }

  /**
   * @return a {@link Communication} with required fields set
   */
  public Communication basic() {
    Communication c = new Communication();
    c.setUuid(this.g.next());
    c.setType("document");
    c.setId("example-comm-id");
    c.setText(text);
    c.setStartTime(Timing.currentUTCTime());
    c.setMetadata(annotationMetadata());
    return c;
  }

  /**
   * @return a {@link Communication} including one {@link Section}, one
   * {@link Sentence}, and one {@link Tokenization} with a populated {@link TokenList}
   */
  public Communication tokenized() {
    Communication basic = this.basic();
    Section sect = this.section();
    Sentence st = this.sentence();
    Tokenization tkz = this.tokenization();
    st.setTokenization(tkz);
    sect.addToSentenceList(st);
    basic.addToSectionList(sect);
    return basic;
  }

  private static TextSpan textSpan() {
    TextSpan ts = new TextSpan(0, text.length());
    return ts;
  }

  /**
   * @return a {@link Section} valid in the context of these {@link Communication}s
   */
  public Section section() {
    Section s = new Section();
    s.setUuid(this.g.next());
    s.setKind("passage");
    s.setTextSpan(textSpan());
    return s;
  }

  /**
   *
   * @return a {@link Sentence} valid in the context of these {@link Communication}s
   */
  public Sentence sentence() {
    Sentence st = new Sentence();
    st.setUuid(this.g.next());
    st.setTextSpan(textSpan());
    return st;
  }

  /**
   *
   * @return a {@link Tokenization} with two {@link Token}s inside a {@link TokenList},
   * valid for these {@link Communication}s
   */
  public Tokenization tokenization() {
    Tokenization tkz = new Tokenization();
    tkz.setUuid(this.g.next());
    TokenList tl = new TokenList();
    {
      Token tk = new Token();
      tk.setText(hello);
      tk.setTextSpan(new TextSpan(0, hello.length()));
      tl.addToTokenList(tk);
    }
    {
      Token tk = new Token();
      tk.setText(world);
      // account for whitespace by adding 1
      final int offset = hello.length() + 1;
      tk.setTextSpan(new TextSpan(offset, offset + world.length()));
      tl.addToTokenList(tk);
    }

    tkz.setKind(TokenizationKind.TOKEN_LIST);
    tkz.setMetadata(annotationMetadata());
    return tkz;
  }
}
