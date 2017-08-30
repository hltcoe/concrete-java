package edu.jhu.hlt.concrete.lucene.pretokenized;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;

public class IndexAnalyzer extends TokenizedAnalyzerBase {

  /** Builds an analyzer with the given stop words.
   * @param stopWords stop words */
  public IndexAnalyzer(CharArraySet stopWords) {
    super(stopWords);
  }

  /** Builds an analyzer with the default stop words ({@link #STOP_WORDS_SET}).
   */
  public IndexAnalyzer() {
    super();
  }

  @Override
  protected TokenStreamComponents createComponents(final String fieldName) {
    final KeywordTokenizer src = new KeywordTokenizer();
    TokenStream tok = new StandardFilter(src);
    tok = new LowerCaseFilter(tok);
    tok = new StopFilter(tok, stopwords);
    return new TokenStreamComponents(src, tok);
  }
}
