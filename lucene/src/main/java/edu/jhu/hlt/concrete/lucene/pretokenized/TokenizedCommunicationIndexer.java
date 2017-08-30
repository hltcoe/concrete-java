package edu.jhu.hlt.concrete.lucene.pretokenized;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.lucene.LuceneCommunicationIndexer;
import edu.jhu.hlt.concrete.miscommunication.MiscCommunication;

public class TokenizedCommunicationIndexer implements LuceneCommunicationIndexer {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenizedCommunicationIndexer.class);

  private final Directory luceneDir;
  private final Analyzer analyzer;
  private final IndexWriter writer;

  public TokenizedCommunicationIndexer(Path path) throws IOException {
    LOGGER.debug("Creating directory object for " + path.toString());
    this.luceneDir = FSDirectory.open(path);
    this.analyzer = new IndexAnalyzer();
    IndexWriterConfig icfg = new IndexWriterConfig(this.analyzer);
    icfg.setOpenMode(OpenMode.CREATE);
    this.writer = new IndexWriter(luceneDir, icfg);
  }

  @Override
  public void add(Communication c) throws IOException {
    List<Document> docs = new ArrayList<Document>();
    if (c.isSetSectionList()) {
      for (Section section : c.getSectionList()) {
        if (section.isSetSentenceList()) {
          for (Sentence sentence : section.getSentenceList()) {
            docs.add(new TokenizedDocumentableSentence(c, sentence).getDocument());
          }
        }
      }
    }
    this.writer.addDocuments(docs);
  }

  @Override
  public void add(MiscCommunication mc) throws IOException {
    throw new UnsupportedOperationException("MiscCommunications do not have default support for tokens.");
  }

  @Override
  public void commit() throws IOException {
    this.writer.commit();
  }

  @Override
  public void close() throws IOException {
    this.writer.close();
    this.analyzer.close();
    this.luceneDir.close();
  }

}
