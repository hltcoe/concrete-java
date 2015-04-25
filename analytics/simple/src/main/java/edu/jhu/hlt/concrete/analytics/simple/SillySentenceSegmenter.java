/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.analytics.base.Analytic;
import edu.jhu.hlt.concrete.analytics.base.AnalyticException;
import edu.jhu.hlt.concrete.sentence.SentenceFactory;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.SuperTextSpan;
import edu.jhu.hlt.concrete.util.Timing;

/**
 * An example of how to generate {@link SentenceSegmentationCollection}s. Probably
 * only useful as an example.  
 */
public class SillySentenceSegmenter implements Analytic {

  public static final Pattern DEFAULT_SENTENCE_PATTERN = Pattern.compile("[a-zA-Z0-9 ,']+[.?!]+");
  private final Pattern splitPattern;
  
  public SillySentenceSegmenter() {
    this.splitPattern = DEFAULT_SENTENCE_PATTERN;
  }
  
  /**
   * Given some text, generate a {@link List} of {@link Sentence} objects given the {@link Pattern}
   * for this class, which is:
   * 
   * <pre>
   * [a-zA-Z0-9 ']+[.?!]+
   * </pre>
   * 
   * @param s - The {@link String} from which to generate {@link Sentence}s
   * @return a {@link List} of {@link Sentence} objects
   */
  public List<Sentence> generateSentencesFromText(String s) {
    List<Sentence> sentList = new ArrayList<Sentence>();
    Matcher m = this.splitPattern.matcher(s);
    while(m.find()) {
      int start = m.start();
      int end = m.end();

      TextSpan ts = new TextSpan(start, end);
      Sentence sent = SentenceFactory.create();
      sent.setTextSpan(ts);
      sentList.add(sent);
    }
    
    return sentList;
  }

  @Override
  public Communication annotate(Communication comm) throws AnalyticException {
    final Communication cpy = new Communication(comm);
    List<Section> sectionList = cpy.getSectionList();
    if (sectionList == null || sectionList.isEmpty()) {
      throw new AnalyticException("Communication SectionSegmentation does not have at least one Section; "
          + "cannot generate a SentenceSegmentationCollection from it.");
    }

    for (Section s : sectionList) {
      TextSpan ts = s.getTextSpan();
      SuperTextSpan sts = new SuperTextSpan(ts, cpy);
      String sectionText = sts.getText();
      List<Sentence> sentList = this.generateSentencesFromText(sectionText);
      s.setSentenceList(sentList);
    }

    return cpy;

  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return Timing.currentLocalTime();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolName()
   */
  @Override
  public String getToolName() {
    return SillySentenceSegmenter.class.getSimpleName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolNotes()
   */
  @Override
  public List<String> getToolNotes() {
    return new ArrayList<>();
  }
}
