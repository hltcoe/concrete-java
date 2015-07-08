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
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.miscommunication.MiscommunicationException;
import edu.jhu.hlt.concrete.miscommunication.sectioned.NonSentencedSectionedCommunication;
import edu.jhu.hlt.concrete.random.RandomConcreteFactory;
import edu.jhu.hlt.concrete.section.SingleSectionSegmenter;
import edu.jhu.hlt.concrete.sentence.SentenceFactory;

/**
 *
 */
public class NonSentencedSectionedCommunicationTest {

  RandomConcreteFactory rcf = new RandomConcreteFactory(1234L);

  @Rule
  public TemporaryFolder tf = new TemporaryFolder();

  @Test
  public void singleSection() throws Exception {
    final Communication c = rcf.communication();
    new WritableCommunication(c).writeToFile(tf.getRoot().toPath(), true);
    Section s = SingleSectionSegmenter.createSingleSection(c, "Passage");
    c.addToSectionList(s);

    new NonSentencedSectionedCommunication(c);
  }

  @Test(expected=MiscommunicationException.class)
  public void hasSentence() throws Exception {
    final Communication c = rcf.communication();
    new WritableCommunication(c).writeToFile(tf.getRoot().toPath(), true);
    Section s = SingleSectionSegmenter.createSingleSection(c, "Passage");

    Sentence st = SentenceFactory.create();
    st.setTextSpan(s.getTextSpan());
    s.addToSentenceList(st);
    c.addToSectionList(s);

    new NonSentencedSectionedCommunication(c);
  }
}
