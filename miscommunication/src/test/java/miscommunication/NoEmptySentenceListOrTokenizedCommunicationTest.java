/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package miscommunication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.communications.CommunicationFactory;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.sentenced.NoEmptySentenceListOrTokenizedCommunication;
import edu.jhu.hlt.concrete.sentence.SentenceFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.tift.Tokenizer;

/**
 *
 */
public class NoEmptySentenceListOrTokenizedCommunicationTest {

  @Test
  public void noSentences() throws Exception {
    Communication c = this.getNoSentenceCommunication();
    assertTrue(c.getSectionListSize() > 0);
    assertFalse(c.getSectionList().stream().anyMatch(sect -> sect.isSetSentenceList()));
    new NoEmptySentenceListOrTokenizedCommunication(c);
  }

  private Communication getNoSentenceCommunication() throws ConcreteException {
    return CommunicationFactory.create("id_1", "This is a sample test block.", "Passage");
  }

  private Communication getSentencedCommunication() throws ConcreteException {
    Communication c = this.getNoSentenceCommunication();
    Sentence st = SentenceFactory.create();
    Section ptr = c.getSectionListIterator().next();
    TextSpan ts = new TextSpan(ptr.getTextSpan());
    st.setTextSpan(ts);
    ptr.addToSentenceList(st);
    return c;
  }

  private Communication getTokenizedCommunication() throws ConcreteException {
    Communication c = this.getSentencedCommunication();
    Tokenization tkz = Tokenizer.PTB.tokenizeToConcrete(c.getText(), 0);
    c.getSectionListIterator().next().getSentenceListIterator().next().setTokenization(tkz);
    return c;
  }

  @Test
  public void noTokenizedSentences() throws Exception {
    Communication c = this.getSentencedCommunication();
    assertTrue(c.getSectionListSize() > 0);
    assertTrue(c.getSectionList().stream().anyMatch(sect -> sect.isSetSentenceList()));
    new NoEmptySentenceListOrTokenizedCommunication(c);
  }

  @Test(expected=MiscommunicationException.class)
  public void tokenizedSentences() throws Exception {
    Communication c = this.getTokenizedCommunication();
    assertTrue(c.getSectionListSize() > 0);
    assertTrue(c.getSectionList().stream().anyMatch(sect -> sect.isSetSentenceList()));
    assertTrue(c.getSectionList().stream().flatMap(st -> st.getSentenceList().stream()).anyMatch(st -> st.isSetTokenization()));
    new NoEmptySentenceListOrTokenizedCommunication(c);
  }

  @Test(expected=MiscommunicationException.class)
  public void emptySentences() throws Exception {
    Communication c = this.getSentencedCommunication();
    c.getSectionListIterator().next().setSentenceList(new ArrayList<>());
    assertTrue(c.getSectionListSize() > 0);
    assertTrue(c.getSectionList().stream().anyMatch(sect -> sect.isSetSentenceList()));
    new NoEmptySentenceListOrTokenizedCommunication(c);
  }
}
