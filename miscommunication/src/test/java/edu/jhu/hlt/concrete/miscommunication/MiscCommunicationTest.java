package edu.jhu.hlt.concrete.miscommunication;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationMetadata;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.TweetInfo;
import edu.jhu.hlt.concrete.TwitterUser;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

public class MiscCommunicationTest {

  final static String txt = "Hello world!";
  final static String otxt = "this is silly";

  final AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
  final AnalyticUUIDGenerator g = f.create();


  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCreate() throws Exception {
    Communication c = new Communication();
    c.setId("dumb");
    c.setUuid(this.g.next());
    c.setText(txt);
    CommunicationMetadata cmd = new CommunicationMetadata();
    TweetInfo ti = new TweetInfo();
    TwitterUser tu = new TwitterUser();
    tu.setId(410249214L);
    ti.setUser(tu);
    cmd.setTweetInfo(ti);
    TextSpan ts = new TextSpan(0, txt.length());
    Sentence st = new Sentence();
    st.setUuid(UUIDFactory.newUUID());
    st.setTextSpan(ts);

    Section s = new SectionFactory(g).fromTextSpan(ts, "passage");
    s.addToSentenceList(st);
    c.addToSectionList(s);
    c.setCommunicationMetadata(cmd);

    MiscCommunication mc = MiscCommunication.create(c);
    assertEquals("dumb", mc.getId().getContent());
    assertEquals(410249214L, mc.getAuthorTwitterID().get().longValue());
  }
}
