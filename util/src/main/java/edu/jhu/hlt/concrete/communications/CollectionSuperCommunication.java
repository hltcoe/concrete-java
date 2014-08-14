/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.communications;

import java.util.ArrayList;
import java.util.Map;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.SentenceSegmentationCollection;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.TokenizationCollection;
import edu.jhu.hlt.concrete.UUID;

/**
 * @author max
 *
 */
public class CollectionSuperCommunication extends SuperCommunication {

  protected final SentenceSegmentationCollection ssc;
  protected final TokenizationCollection tc;
  
  /**
   * @param comm
   */
  public CollectionSuperCommunication(Communication comm) {
    super(comm);
    this.ssc = this.generateSentSegColl();
    this.tc = this.generateTokenizationColl();
  }
  
  private SentenceSegmentationCollection generateSentSegColl() {
    SentenceSegmentationCollection ssc = new SentenceSegmentationCollection();
    ssc.setMetadata(new AnnotationMetadata()
        .setTool("CollectionSuperCommunication")
        .setTimestamp(System.currentTimeMillis()));
    
    Map<UUID, Section> idToSectionMap = this.generateSectionIdToSectionMap();
    for (Section s : idToSectionMap.values())
      for (SentenceSegmentation ss : s.getSentenceSegmentation())
        ssc.addToSentSegList(ss);
    
    if (!ssc.isSetSentSegList())
      ssc.setSentSegList(new ArrayList<SentenceSegmentation>());
    
    return ssc;
  }
  
  private TokenizationCollection generateTokenizationColl() {
    TokenizationCollection tc = new TokenizationCollection();
    tc.setMetadata(this.getMetadata());
    
    Map<UUID, Sentence> idToSentenceMap = this.generateSentenceIdToSectionMap();
    for (Sentence s : idToSentenceMap.values())
      for (Tokenization ss : s.getTokenizationList())
        tc.addToTokenizationList(ss);
    
    if (!tc.isSetTokenizationList())
      tc.setTokenizationList(new ArrayList<Tokenization>());
    
    return tc;
  }
  
  private AnnotationMetadata getMetadata() {
    return new AnnotationMetadata()
      .setTool("CollectionSuperCommunication")
      .setTimestamp(System.currentTimeMillis());
  }
}
