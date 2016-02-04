/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package miscommunication;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.sentenced.NonTokenizedSentencedCommunication;
import edu.jhu.hlt.concrete.random.RandomConcreteFactory;
import edu.jhu.hlt.concrete.section.SingleSectionSegmenter;
import edu.jhu.hlt.concrete.sentence.SentenceFactory;
import edu.jhu.hlt.tift.Tokenizer;

/**
 *
 */
public class NonTokenizedSentencedCommunicationTest {

  RandomConcreteFactory rcf = new RandomConcreteFactory(1234L);

  @Rule
  public TemporaryFolder tf = new TemporaryFolder();

  @Test(expected=MiscommunicationException.class)
  public void singleSection() throws Exception {
    final Communication c = rcf.communication();
    new WritableCommunication(c).writeToFile(tf.getRoot().toPath(), true);
    Section s = SingleSectionSegmenter.createSingleSection(c, "Passage");
    c.addToSectionList(s);

    new NonTokenizedSentencedCommunication(c);
  }

  @Test
  public void hasSentence() throws Exception {
    final Communication c = rcf.communication();
    new WritableCommunication(c).writeToFile(tf.getRoot().toPath(), true);
    Section s = SingleSectionSegmenter.createSingleSection(c, "Passage");

    Sentence st = new SentenceFactory(c).create();
    st.setTextSpan(s.getTextSpan());
    s.addToSentenceList(st);
    c.addToSectionList(s);

    new NonTokenizedSentencedCommunication(c);
  }

  @Test(expected=MiscommunicationException.class)
  public void tokenized() throws Exception {
    final Communication c = rcf.communication();
    new WritableCommunication(c).writeToFile(tf.getRoot().toPath(), true);
    Section s = SingleSectionSegmenter.createSingleSection(c, "Passage");

    Sentence st = new SentenceFactory(c).create();
    st.setTextSpan(s.getTextSpan());

    Tokenization tkz = Tokenizer.PTB.tokenizeToConcrete(c.getText(), 0);
    st.setTokenization(tkz);

    s.addToSentenceList(st);
    c.addToSectionList(s);

    new NonTokenizedSentencedCommunication(c);
  }
}
