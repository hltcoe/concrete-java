package edu.jhu.hlt.concrete.lucene.pretokenized;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.util.List;

import org.apache.lucene.document.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationMetadata;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.lucene.ConcreteLuceneConstants;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.tift.Tokenizer;

public class PretokenizedEndToEndTest {
  final AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
  final AnalyticUUIDGenerator g = f.create();

  @Rule
  public final TemporaryFolder tf = new TemporaryFolder();
  public Path p;

  @Before
  public void setUp() throws Exception {
    this.p = tf.getRoot().toPath();
  }

  private Communication mockCommunication(String id, String text) throws ConcreteException {
    Communication c = new Communication();
    c.setId(id);
    c.setUuid(this.g.next());
    c.setText(text);
    CommunicationMetadata cmd = new CommunicationMetadata();
    TextSpan ts = new TextSpan(0, text.length());
    Sentence st = new Sentence();
    st.setTextSpan(ts);
    st.setUuid(g.next());
    st.setTokenization(Tokenizer.WHITESPACE.tokenizeToConcrete(text));

    Section s = new SectionFactory(g).fromTextSpan(ts, "passage");
    s.addToSentenceList(st);

    c.addToSectionList(s);
    c.setCommunicationMetadata(cmd);

    return c;
  }

  @Test
  public void test() throws Exception {
    try(TokenizedCommunicationIndexer idxer = new TokenizedCommunicationIndexer(this.p)) {
      idxer.add(this.mockCommunication("bar", "this is silly"));
      idxer.add(this.mockCommunication("qux", "hello world"));
      idxer.add(this.mockCommunication("punct", "Dr . No"));
      idxer.add(this.mockCommunication("baz", "the wi-fi is not very good here, in fact, it is silly"));
      idxer.add(this.mockCommunication("chinese1", "他们 赋有 理性 和良"));
      idxer.add(this.mockCommunication("email", "send a message to me@example.com please"));
    }

    try(TokenizedCommunicationSearcher search = new TokenizedCommunicationSearcher(this.p)) {
      // won't return items if no match
      assertEquals(0, search.search("notexist", 5).totalHits);

      // sanity check on a single matching document
      List<Document> docs = search.searchDocuments("world", 50);
      assertEquals(1, docs.size());
      assertEquals("qux", docs.get(0).get(ConcreteLuceneConstants.COMM_ID_FIELD));

      // rank by relevance
      docs = search.searchDocuments("silly", 50);
      assertEquals(2, docs.size());
      assertEquals("bar", docs.get(0).get(ConcreteLuceneConstants.COMM_ID_FIELD));
      assertEquals("baz", docs.get(1).get(ConcreteLuceneConstants.COMM_ID_FIELD));

      // case does not matter
      docs = search.searchDocuments("Hello", 50);
      assertEquals(1, docs.size());
      assertEquals("qux", docs.get(0).get(ConcreteLuceneConstants.COMM_ID_FIELD));

      // indexing and searching work with hyphenated words
      docs = search.searchDocuments("wi-fi", 50);
      assertEquals(1, docs.size());
      assertEquals("baz", docs.get(0).get(ConcreteLuceneConstants.COMM_ID_FIELD));

      // indexing and searching work with email addresses
      docs = search.searchDocuments("me@example.com", 50);
      assertEquals(1, docs.size());
      assertEquals("email", docs.get(0).get(ConcreteLuceneConstants.COMM_ID_FIELD));

      // punctuation is filtered
      docs = search.searchDocuments(".", 50);
      assertEquals(0, docs.size());

      // chinese does not get tokenized by analyzer
      docs = search.searchDocuments("赋有", 50);
      assertEquals(1, docs.size());
      assertEquals("chinese1", docs.get(0).get(ConcreteLuceneConstants.COMM_ID_FIELD));
    }

  }
}
