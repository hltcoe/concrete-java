package edu.jhu.hlt.concrete.ingest;

import java.util.List;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationKind;

public class CommunicationUtils {

  /**
   * Given a {@link Communication} which has {@link Token}s that have their
   * text field set, but not their {@link TextSpan}, build a String for the
   * entire document and create {@link TextSpan}s that point into that for
   * everything above {@link Token}. Sentences go on their own line, and their
   * is an empty line at the end of every section.
   *
   * NOTE: This method should only be used in cases where there is no original
   * text (e.g. CoNLL data which comes word-segmented).
   */
  public static void projectTokenTextSpansUpwards(Communication c) {
    if (c.isSetText())
      throw new IllegalArgumentException("text is already set");
    StringBuilder sb = new StringBuilder();
    for (Section sect : c.getSectionList()) {
      int sectionStart = sb.length();
      for (Sentence sent : sect.getSentenceList()) {
        Tokenization tok = sent.getTokenization();
        if (!TokenizationKind.TOKEN_LIST.equals(tok.getKind()))
          throw new IllegalArgumentException("only token lists are supported");
        int sentenceStart = sb.length();
        List<Token> toks = tok.getTokenList().getTokenList();
        for (int i = 0; i < toks.size(); i++) {
          if (i > 0)
            sb.append(' ');
          Token t = toks.get(i);
          if (!t.isSetText())
            throw new IllegalArgumentException("Token text is not set!");
          int start = sb.length();
          sb.append(t.getText());
          int end = sb.length();
          t.setTextSpan(new TextSpan(start, end));
        }
        int sentenceEnd = sb.length();
        if (sent.isSetTextSpan()) {
          boolean s = sentenceStart == sent.getTextSpan().getStart();
          boolean e = sentenceEnd == sent.getTextSpan().getEnding();
          if (!s || !e) {
            throw new RuntimeException("incompatible existing Sentence.textSpan!"
                + " existingStart=" + sent.getTextSpan().getStart()
                + " existingEnd=" + sent.getTextSpan().getEnding()
                + " computedStart=" + sentenceStart
                + " computedEnd=" + sentenceEnd);
          }
        } else {
          sent.setTextSpan(new TextSpan(sentenceStart, sentenceEnd));
        }
        sb.append('\n');
      }
      int sectionEnd = sb.length();
      if (sect.isSetTextSpan()) {
        boolean s = sectionStart == sect.getTextSpan().getStart();
        boolean e = sectionEnd == sect.getTextSpan().getEnding();
        if (!s || !e) {
          throw new RuntimeException("incompatible existing Sentence.textSpan!"
              + " existingStart=" + sect.getTextSpan().getStart()
              + " existingEnd=" + sect.getTextSpan().getEnding()
              + " computedStart=" + sectionStart
              + " computedEnd=" + sectionEnd);
        }
      } else {
        sect.setTextSpan(new TextSpan(sectionStart, sectionEnd));
      }
      sb.append('\n');
    }
    c.setText(sb.toString());
  }
}
