package edu.jhu.hlt.concrete.ingesters.kbp2015;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.TokenRefSequence;
import edu.jhu.hlt.concrete.Tokenization;

/**
 * Try to match a {@link TextSpan} up to a {@link Tokenization}.
 *
 * @author travis
 */
public class TextSpanToTokens {

  private static class Argmin<T> {
    private T bestItem;
    private double bestScore;
    public void offer(T item, double score) {
      if (item == null || Double.isInfinite(score) || Double.isNaN(score))
        throw new IllegalArgumentException();
      if (bestItem == null || score < bestScore) {
        bestItem = item;
        bestScore = score;
      }
    }
    public T get() {
      assert bestItem != null;
      return bestItem;
    }
  }

  private static class TokPtr {
    public final Tokenization tok;
    public final int index;
    public TokPtr(Tokenization tok, int index) {
      this.tok = tok;
      this.index = index;
    }
  }

  /**
   * Finds the {@link TokenRefSequence} (token indices) in the given {@link
   * Communication} which is closest to the given {@link TextSpan} (character
   * indices). Throws an exception if start and end don't appear in the same
   * sentence.
   */
  public static TokenRefSequence resolve(TextSpan ts, Communication c) {
    Argmin<TokPtr> start = new Argmin<>();
    Argmin<TokPtr> end = new Argmin<>();
    for (Section sect : c.getSectionList()) {
      for (Sentence sent : sect.getSentenceList()) {
        Tokenization tok = sent.getTokenization();
        for (Token t : tok.getTokenList().getTokenList()) {
          TextSpan tss = t.getTextSpan();
          double errStart = Math.pow(tss.getStart() - ts.getStart(), 2);
          start.offer(new TokPtr(tok, tss.getStart()), errStart);
          double errEnd = Math.pow(tss.getEnding() - ts.getEnding(), 2);
          start.offer(new TokPtr(tok, tss.getEnding()), errEnd);
        }
      }
    }

    TokPtr s = start.get();
    TokPtr e = end.get();
    if (s == null || e == null)
      throw new RuntimeException("empty Communication?");
    if (s.tok != e.tok)
      throw new RuntimeException("start and end are in different sentences");
    if (s.index >= e.index)
      throw new RuntimeException("thought that empty seq was optimal");
    TokenRefSequence trs = new TokenRefSequence();
    trs.setTokenizationId(s.tok.getUuid());
    for (int i = s.index; i <= e.index; i++)
      trs.addToTokenIndexList(i);
    return trs;
  }
}
