package edu.jhu.hlt.concrete.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;

/**
 * A utility class that allows searching over a pre-built Lucene index.
 */
public class ConcreteLuceneSearcher implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteLuceneSearcher.class);

  private Directory dir;
  private IndexReader idxr;
  private IndexSearcher search;
  private Analyzer analyzer;

  public ConcreteLuceneSearcher(Path p) throws IOException {
    this(p, new StandardAnalyzer());
  }

  public ConcreteLuceneSearcher(Path p, Analyzer analyzer) throws IOException {
    this.dir = FSDirectory.open(p);
    this.idxr = DirectoryReader.open(this.dir);
    this.search = new IndexSearcher(this.idxr);
    this.analyzer = analyzer;
  }

  public TopDocs search(String query, int maxDocs) throws ParseException, IOException {
    Query q = this.createLuceneQuery(query);
    LOGGER.debug("Got query: {}", q.toString());
    TopDocs topDocs = this.search.search(q, maxDocs);
    return topDocs;
  }

  public List<Document> searchDocuments(String query, int maxDocs) throws ParseException, IOException {
    TopDocs td = this.search(query, maxDocs);
    ImmutableList.Builder<Document> db = new ImmutableList.Builder<>();
    for (ScoreDoc sd : td.scoreDocs)
      db.add(this.search.doc(sd.doc));
    return db.build();
  }

  public List<Document> searchDocuments(String terms, long authorId, int maxDocs) throws ParseException, IOException {
    String fullQ = new StringBuilder(terms)
        .append(" AND author-id:")
        .append(authorId)
        .toString();
    return this.searchDocuments(fullQ, maxDocs);
  }

  public Document get(int docId) throws IOException {
    return this.search.doc(docId);
  }

  private Query createLuceneQuery(String queryText) throws ParseException {
    QueryParser queryParser = new QueryParser(ConcreteLuceneConstants.TEXT_FIELD, analyzer);
    return queryParser.parse(queryText);
  }

  @Override
  public void close() throws IOException {
    this.analyzer.close();
    this.idxr.close();
    this.dir.close();
  }

  private static final class Opts {
    @Parameter(description = "Query terms")
    List<String> queryTerms;

    @Parameter(names = "--index-path", description = "Folder containing the lucene index.",
        required = true)
    String pathStr;

    @Parameter(names = "--author-id", description = "Twitter ID of author to filter results.")
    Long authorId;

    @Parameter(names = "--max-results", description = "The maximum number of results to return.")
    int maxResults = 500;

    @Parameter(names = "--help", description = "Print help message and exit.",
        help = true)
    boolean help;
  }

  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    Opts o = new Opts();
    JCommander jc = new JCommander(o, args);
    if (o.help) {
      jc.usage();
      return;
    }

    List<String> terms = o.queryTerms;
    if (terms.isEmpty()) {
      System.out.println("Query terms are required.");
      System.exit(1);
    } else {
      String query = Joiner.on(' ').join(terms);
      Optional<Long> oid = Optional.ofNullable(o.authorId);
      try (ConcreteLuceneSearcher search = new ConcreteLuceneSearcher(Paths.get(o.pathStr))) {
        List<Document> docs;
        if (oid.isPresent())
          docs = search.searchDocuments(query, oid.get(), o.maxResults);
        else
          docs = search.searchDocuments(query, o.maxResults);

        for (Document doc : docs)
          System.out.println(doc.get(ConcreteLuceneConstants.COMM_ID_FIELD));

      } catch (ParseException | IOException e) {
        LOGGER.error("Caught an exception during querying.", e);
      }
    }
  }
}
