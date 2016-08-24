package edu.jhu.hlt.concrete.lucene;

import static org.junit.Assert.*;

import java.nio.file.Path;

import org.apache.lucene.search.TopDocs;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationMetadata;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.TweetInfo;
import edu.jhu.hlt.concrete.TwitterUser;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

public class EndToEndTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(EndToEndTest.class);

  final AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
  final AnalyticUUIDGenerator g = f.create();

  @Rule
  public final TemporaryFolder tf = new TemporaryFolder();
  public Path p;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    this.p = tf.getRoot().toPath();
  }

  @After
  public void tearDown() throws Exception {
  }

  private Communication mockCommunication(String id, String text, long authorId) throws ConcreteException {
    Communication c = new Communication();
    c.setId(id);
    c.setUuid(this.g.next());
    c.setText(text);
    CommunicationMetadata cmd = new CommunicationMetadata();
    TweetInfo ti = new TweetInfo();
    TwitterUser tu = new TwitterUser();
    tu.setId(authorId);
    ti.setUser(tu);
    cmd.setTweetInfo(ti);
    TextSpan ts = new TextSpan(0, text.length());
    Sentence st = new Sentence();
    st.setTextSpan(ts);
    st.setUuid(g.next());

    Section s = new SectionFactory(g).fromTextSpan(ts, "passage");
    s.addToSentenceList(st);

    c.addToSectionList(s);
    c.setCommunicationMetadata(cmd);

    return c;
  }

  @Test
  public void test() throws Exception {
    try(LuceneCommunicationIndexer idxer = new NaiveConcreteLuceneIndexer(this.p)) {
      idxer.add(this.mockCommunication("bar", "this is silly", 1234L));
      idxer.add(this.mockCommunication("qux", "hello world", 1234L));
      idxer.add(this.mockCommunication("baz", "the wi-fi is not very good here, in fact, it is silly", 4321L));
      idxer.add(this.mockCommunication("heh", "grrr wtf mate", 40000004L));
    }

    try(ConcreteLuceneSearcher search = new ConcreteLuceneSearcher(this.p)) {
      assertEquals(0, search.search("failfish", 5).totalHits);
      TopDocs td = search.search("world", 50);
      assertEquals(1, td.totalHits);
      int did = td.scoreDocs[0].doc;
      LOGGER.info("Got TD: {}", search.get(did).toString());
      td = search.search("silly", 5);
      assertEquals(2, td.totalHits);
      td = search.search("silly AND author-id:4321", 5);
      assertEquals(1, td.totalHits);
    }
  }
}
