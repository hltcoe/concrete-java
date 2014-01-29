package edu.jhu.hlt.concrete.examples;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Sentence;

public class SillySentenceSegmenterTest {

  private static final Logger logger = LoggerFactory.getLogger(SillySentenceSegmenterTest.class);
  
  @Test
  public void exampleSentenceSplit() {
    String text = "hello. This is a sample sentence, is it? Very useful!";
    logger.info("Generating sentences for this sentence: ");
    logger.info(text);
    
    SillySentenceSegmenter sss = new SillySentenceSegmenter();
    List<Sentence> sentList = sss.generateSentencesFromText(text);
    for (Sentence s : sentList) 
      logger.info("Got sentence: " + s.toString());
  }
}
