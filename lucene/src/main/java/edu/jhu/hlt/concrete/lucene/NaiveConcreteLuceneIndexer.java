package edu.jhu.hlt.concrete.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.TwitterUser;
import edu.jhu.hlt.concrete.miscommunication.MiscCommunication;

/**
 * This class builds a Lucene index based on Concrete {@link Sentence} objects.
 * It uses the {@link TextSpan}s of the sentence objects, along with the
 * {@link Communication#getText()} method, to create sentences with text.
 * Along with the {@link Communication#getId()} of the sentence, this allows a client
 * to query relevant Concrete sentences for retrieval alongside their parent
 * Communications.
 * <br><br>
 * Necessarily, this class requires communication objects with the following:
 * <ul>
 * <li>communications with the <code>text</code> field set</li>
 * <li>sections</li>
 * <li>sentences with valid text spans</li>
 * </ul>
 * <br><br>
 * This class optionally uses the {@link TwitterUser#getId()} field for
 * author-based lookups. This field is not required, but will be used
 * if present.
 * <br><br>
 * Finally, it is important to note that the {@link StandardAnalyzer}
 * powers this index. As a result, clients and search implementations
 * should also use the same analyzer.
 *
 * @see LuceneCommunicationIndexer
 */
public class NaiveConcreteLuceneIndexer implements AutoCloseable, LuceneCommunicationIndexer {

  private static final Logger LOGGER = LoggerFactory.getLogger(NaiveConcreteLuceneIndexer.class);

  private final Path p;
  private final Directory luceneDir;
  private final Analyzer analyzer;
  private final IndexWriter writer;

  public NaiveConcreteLuceneIndexer(Path p) throws IOException {
    this.p = p;
    LOGGER.debug("Creating directory object.");
    this.luceneDir = FSDirectory.open(this.p);
    this.analyzer = new StandardAnalyzer();
    IndexWriterConfig icfg = new IndexWriterConfig(this.analyzer);
    icfg.setOpenMode(OpenMode.CREATE_OR_APPEND);
    this.writer = new IndexWriter(luceneDir, icfg);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.lucene.LuceneCommunicationIndexer#add(edu.jhu.hlt.concrete.miscommunication.MiscCommunication)
   */
  @Override
  public void add (MiscCommunication mc) throws IOException {
    List<Document> docs = mc.getSections()
      .values()
      .stream()
      .flatMap(ms -> ms.getIdToSentenceMap().values().stream())
      .map(ms -> DocumentableSentence.create(ms, mc.getAuthorTwitterID()))
      .map(DocumentableSentence::getDocument)
      .collect(Collectors.toList());
    this.writer.addDocuments(docs);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws IOException {
    this.writer.close();
    this.analyzer.close();
    this.luceneDir.close();
  }

  @Override
  public void commit() throws IOException {
    this.writer.commit();
  }
}
