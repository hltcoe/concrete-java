package edu.jhu.hlt.concrete.lucene.pretokenized;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.concrete.lucene.ConcreteLuceneConstants;
import edu.jhu.hlt.concrete.lucene.LuceneCommunicationSearcher;


public class TokenizedCommunicationSearcher implements LuceneCommunicationSearcher {
  private static final Logger LOGGER = LoggerFactory.getLogger(TokenizedCommunicationSearcher.class);

  private Directory luceneDir;
  private IndexReader idxr;
  private IndexSearcher search;
  private Analyzer analyzer;

  public TokenizedCommunicationSearcher(Path path) throws IOException {
    this(path, new SearchAnalyzer());
  }

  public TokenizedCommunicationSearcher(Path path, Analyzer analyzer) throws IOException {
    this.luceneDir = FSDirectory.open(path);
    this.idxr = DirectoryReader.open(this.luceneDir);
    this.search = new IndexSearcher(this.idxr);
    this.analyzer = analyzer;
  }

  protected TopDocs search(String query, int maxDocs) throws ParseException, IOException {
    Query q = this.createLuceneQuery(query);
    LOGGER.debug("Got query: {}", q.toString());
    TopDocs topDocs = this.search.search(q, maxDocs);
    return topDocs;
  }

  @Override
  public List<Document> searchDocuments(String query, int maxDocs) throws ParseException, IOException {
    TopDocs td = this.search(query, maxDocs);
    ImmutableList.Builder<Document> db = new ImmutableList.Builder<>();
    for (ScoreDoc sd : td.scoreDocs) {
      db.add(this.search.doc(sd.doc));
    }
    return db.build();
  }

  @Override
  public List<Document> searchDocuments(String query, long authorId, int maxDocs) throws ParseException, IOException {
    throw new UnsupportedOperationException("Pre-tokenized indexes do not support twitter author id yet.");
  }

  private Query createLuceneQuery(String queryText) throws ParseException {
    QueryParser queryParser = new QueryParser(ConcreteLuceneConstants.TEXT_FIELD, analyzer);
    return queryParser.parse(queryText);
  }

  @Override
  public void close() throws IOException {
    this.analyzer.close();
    this.idxr.close();
    this.luceneDir.close();
  }

}
