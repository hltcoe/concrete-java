package edu.jhu.hlt.concrete.lucene;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcreteLuceneSearcher implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteLuceneSearcher.class);

  private Directory dir;
  private IndexReader idxr;
  private IndexSearcher search;
  private Analyzer analyzer;

  public ConcreteLuceneSearcher(Path p) throws IOException {
    this.dir = FSDirectory.open(p);
    this.idxr = DirectoryReader.open(this.dir);
    this.search = new IndexSearcher(this.idxr);
    this.analyzer = new StandardAnalyzer();
  }

  public TopDocs search(String query, int maxDocs) throws ParseException, IOException {
    Query q = this.createLuceneQuery(query);
    LOGGER.info("Got query: {}", q.toString());
    TopDocs topDocs = this.search.search(q, maxDocs);
    return topDocs;
  }

  public Document get(int docId) throws IOException {
    return this.search.doc(docId);
  }

  private Query createLuceneQuery(String queryText) throws ParseException {
    QueryParser queryParser = new QueryParser("text", analyzer);
    return queryParser.parse(queryText);
  }

  @Override
  public void close() throws Exception {
    this.analyzer.close();
    this.idxr.close();
    this.dir.close();
  }
}
