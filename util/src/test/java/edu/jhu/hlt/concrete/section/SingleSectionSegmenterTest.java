package edu.jhu.hlt.concrete.section;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.random.RandomConcreteFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;

public class SingleSectionSegmenterTest {

  RandomConcreteFactory rcf = new RandomConcreteFactory(1234L);
  Communication c = rcf.communication();

  @Test
  public void validComm() throws ConcreteException {
    assertFalse(c.isSetSectionList());
    Section s = SingleSectionSegmenter.createSingleSection(this.c, "passage");
    c.addToSectionList(s);
    assertTrue(c.isSetSectionList());
    assertEquals(1, c.getSectionListSize());
    Section rs = c.getSectionList().get(0);
    assertEquals("passage", rs.getKind());
    TextSpan ts = rs.getTextSpan();
    assertEquals(0, ts.getStart());
    assertEquals(c.getText().length(), ts.getEnding());
  }

  @Test(expected=ConcreteException.class)
  public void noText() throws ConcreteException {
    Communication ws = new Communication(this.c);
    ws.unsetText();
    SingleSectionSegmenter.createSingleSection(ws, "passage");
  }

  @Test(expected=ConcreteException.class)
  public void emptyText() throws ConcreteException {
    Communication ws = new Communication(this.c);
    ws.setText("");
    SingleSectionSegmenter.createSingleSection(ws, "passage");
  }
}
