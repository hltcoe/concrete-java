package edu.jhu.hlt.concrete.lucene.pretokenized;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;

public class SearchAnalyzer extends TokenizedAnalyzerBase {

  /** Builds an analyzer with the given stop words.
   * @param stopWords stop words */
  public SearchAnalyzer(CharArraySet stopWords) {
    super(stopWords);
  }

  /** Builds an analyzer with the default stop words ({@link #STOP_WORDS_SET}).
   */
  public SearchAnalyzer() {
    super();
  }

  @Override
  protected TokenStreamComponents createComponents(final String fieldName) {
    final WhitespaceTokenizer src = new WhitespaceTokenizer();
    TokenStream tok = new StandardFilter(src);
    tok = new LowerCaseFilter(tok);
    tok = new StopFilter(tok, stopwords);
    return new TokenStreamComponents(src, tok);
  }

}
