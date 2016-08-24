package edu.jhu.hlt.concrete.miscommunication;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

public class MiscSentenceTest {

  final String txt = "hello world!";

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
  public void testCreate() {
    Sentence st = new Sentence();
    st.setUuid(UUIDFactory.newUUID());
    TextSpan ts = new TextSpan(0, txt.length());
    st.setTextSpan(ts);
    MiscSentence ms = MiscSentence.create(st, "foo", txt);
    assertEquals("hello world!", ms.getTextSpan().get().getText().getContent());
  }
}
