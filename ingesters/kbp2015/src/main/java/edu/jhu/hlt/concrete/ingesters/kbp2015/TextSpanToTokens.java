package edu.jhu.hlt.concrete.ingesters.kbp2015;

import java.util.Arrays;
import java.util.List;

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
  public static boolean DEBUG = false;

  static int N_RESOLVE_EXACT = 0;
  static int N_RESOLVE_FUZZY = 0;

  private static class Argmin<T> {
    private T bestItem;
    private double bestScore;
    int offers = 0;
    public void offer(T item, double score) {
      if (item == null || Double.isInfinite(score) || Double.isNaN(score))
        throw new IllegalArgumentException();
      if (bestItem == null || score < bestScore) {
        bestItem = item;
        bestScore = score;
      }
      offers++;
    }
    public T get() {
      assert bestItem != null;
      return bestItem;
    }
    public String toString() {
      return "<ArgMin bestScore=" + bestScore + " bestItem="  + bestItem + " offers=" + offers + ">";
    }
  }

  private static class TokPtr {
    public final Tokenization tok;
    public final int charStart, charEnd;
    public final int index;
    public TokPtr(Tokenization tok, TextSpan ts, int index) {
      this(tok, ts.getStart(), ts.getEnding(), index);
    }
    public TokPtr(Tokenization tok, int charStart, int charEnd, int index) {
      this.tok = tok;
      this.charStart = charStart;
      this.charEnd = charEnd;
      this.index = index;
    }
    public String toString() {
      return "<TokPtr " + index + " " + tok.getUuid() + "@" + charStart + "-" + charEnd + ">";
    }
  }

  /**
   * Finds the {@link TokenRefSequence} (token indices) in the given {@link
   * Communication} which is closest to the given {@link TextSpan} (character
   * indices). Throws an exception if start and end don't appear in the same
   * sentence.
   */
  public static TokenRefSequence resolve(TextSpan ts, Communication c) {
    if (DEBUG) {
      System.out.println();
      System.out.println("looking for: " + ts);
      System.out.println("text: " + c.getText().substring(ts.getStart(), ts.getEnding()+1));
    }

    // Try for an exact match first
    for (Section sect : c.getSectionList()) {
      for (Sentence sent : sect.getSentenceList()) {
        Tokenization tok = sent.getTokenization();
        int tokIdx = 0;
        int startIdx = -1;
        int endIdx = -1;
        for (Token t : tok.getTokenList().getTokenList()) {
          TextSpan tss = t.getTextSpan();
          if (tss.getStart() == ts.getStart())
            startIdx = tokIdx;
          if (tss.getEnding() == ts.getEnding())
            endIdx = tokIdx;
          tokIdx++;
        }
        if (startIdx >= 0 && endIdx >= 0) {
          TokenRefSequence trs = new TokenRefSequence();
          trs.setTokenizationId(tok.getUuid());
          for (int i = startIdx; i <= endIdx; i++)
            trs.addToTokenIndexList(i);
          N_RESOLVE_EXACT++;
          return trs;
        }
      }
    }

    N_RESOLVE_FUZZY++;
    String needle = c.getText().substring(ts.getStart(), ts.getEnding()+1);

    Argmin<TokPtr> start = new Argmin<>();
    for (int w : Arrays.asList(50, 500, -1)) {
      for (Section sect : c.getSectionList()) {
        for (Sentence sent : sect.getSentenceList()) {
          Tokenization tok = sent.getTokenization();

          // Maybe skip if the needle isn't anywhere near this tokenization
          if (w >= 0) {
            int nt = tok.getTokenList().getTokenListSize();
            int tokStart = tok.getTokenList().getTokenList().get(0).getTextSpan().getStart();
            int tokEnd = tok.getTokenList().getTokenList().get(nt-1).getTextSpan().getEnding();
            if (ts.getEnding() + w < tokStart)
              continue;
            if (ts.getStart() - w > tokEnd)
              continue;
          }

          int tokIdx = 0;
          for (Token t : tok.getTokenList().getTokenList()) {
            TextSpan tss = t.getTextSpan();
            double errStart = Math.sqrt(Math.abs(tss.getStart() - ts.getStart()));

            int end = Math.min(c.getText().length(), tss.getStart() + needle.length());
            String text = c.getText().substring(tss.getStart(), end);
            errStart += levenshteinDistance(needle, text);

            start.offer(new TokPtr(tok, t.getTextSpan(), tokIdx), errStart);
            tokIdx++;
          }
        }
      }
      if (start.offers > 0)
        break;
    }

    if (start.offers == 0) {
      System.out.println("WARNING: no tokens? " + c.getId() + "\t" + c.getText());
      return null;
    }

    Argmin<TokPtr> end = new Argmin<>();
    Tokenization tok = start.get().tok;
//    int startEnd = start.get().charEnd;
    int startTok = start.get().index;
    int tokIdx = 0;
    for (Token t : tok.getTokenList().getTokenList()) {
      TextSpan tss = t.getTextSpan();
//      if (tss.getEnding() >= startEnd) {
      if (tokIdx >= startTok) {
        double errEnd = Math.sqrt(Math.abs(tss.getEnding() - ts.getEnding()));
        end.offer(new TokPtr(tok, t.getTextSpan(), tokIdx), errEnd);
      }
      tokIdx++;
    }

    TokPtr s = start.get();
    TokPtr e = end.get();
    if (s == null || e == null)
      throw new RuntimeException("empty Communication?");
    if (DEBUG || s.tok != e.tok || s.index > e.index) {
      System.out.println();
      System.out.println("found: " + s.index + "," + e.index);
      System.out.println("start: " + start);
      System.out.println("start: " + c.getText().substring(s.charStart, s.charEnd+1));
      System.out.println("end: " + end);
      System.out.println("end: " + c.getText().substring(e.charStart, e.charEnd+1));
      System.out.println("orig text: " + c.getText().substring(ts.getStart(), ts.getEnding()+1));
      System.out.println("orig text: " + ts);
      if (s.charStart < e.charEnd && s.charStart >= 0 && e.charEnd <= c.getText().length())
        System.out.println("text: " + c.getText().substring(s.charStart, e.charEnd+1));
      else
        System.out.println("text: ERROR, not in [0," + c.getText().length() + ")");
    }
    if (s.tok != e.tok) {
      throw new RuntimeException("start and end are in different sentences,"
          + " ts=" + ts + " start=" + start + " end=" + end);
    }
    if (s.index > e.index) {
      throw new RuntimeException();
    }
    TokenRefSequence trs = new TokenRefSequence();
    trs.setTokenizationId(s.tok.getUuid());
    for (int i = s.index; i <= e.index; i++)
      trs.addToTokenIndexList(i);
    return trs;
  }

  public static int levenshteinDistance(String s1, String s2) {
    int Ni = s1.length();
    int Nj = s2.length();
    if (Ni == 0) return Nj;
    if (Nj == 0) return Ni;
    int[][] table = new int[Ni][Nj];
    int i = 0;
    while (i < Ni) {
      int j = 0;
      while (j < Nj) {
        if      (i == 0 && j == 0) table[i][j] = 0;
        else if (i == 0 && j != 0) table[i][j] = j;
        else if (i != 0 && j == 0) table[i][j] = i;
        else {
          int a = table[i-1][j-1] + ((s1.charAt(i) == s2.charAt(j)) ? 0 : 1);
          int b = table[i-1][j]   + 1;
          int c = table[i][j-1]   + 1;
          if (a <= b && a <= c)
            table[i][j] = a;
          else if (b <= a && b <= c)
            table[i][j] = b;
          else
            table[i][j] = c;
        }
        j = j + 1;
      }
      i = i + 1;
    }
    return table[Ni-1][Nj-1];
  }

  private static String foo(TokPtr start, TokPtr end, Communication c) {
    if (start.tok != end.tok)
      return "spans-sentences-not-implemented";

    List<Token> t = start.tok.getTokenList().getTokenList();
    StringBuilder sb = new StringBuilder();
    for (int i = start.index; i <= end.index; i++) {
      if (i > start.index)
        sb.append(' ');
      sb.append(t.get(i).getText());
    }
    return sb.toString();
  }
}
