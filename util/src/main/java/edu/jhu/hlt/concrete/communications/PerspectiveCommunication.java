/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.communications;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.TheoryDependencies;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Attempt to prepare a new "perspective" from a {@link Communication}.
 * 
 * @author max
 */
public class PerspectiveCommunication {

  private final Communication comm;
  
  /**
   * @throws ConcreteException 
   * 
   */
  public PerspectiveCommunication(Communication original, String perspectiveTool) throws ConcreteException {
    if (!original.isSetText())
      throw new ConcreteException("The original communication has to have .text set in order to build a perspective.");
    Communication copy = new Communication(original);

    // Unset .text, then move it to originalText field. 
    copy.unsetText();
    copy.setOriginalText(original.getText());
    
    // Move spans. Unset tokenizations.
    if (copy.isSetSectionList())
      for (Section s : copy.getSectionList()) {
        s.setRawTextSpan(s.getTextSpan());
        s.unsetTextSpan();
        
        if (s.isSetSentenceList())
          for (Sentence ss : s.getSentenceList()) {
            ss.setRawTextSpan(ss.getTextSpan());
            ss.unsetTextSpan();
            
            if (ss.isSetTokenization())
              ss.unsetTokenization();
          }
      }
    
    // Handle metadata.
    AnnotationMetadata mdcp;
    if (copy.isSetMetadata())
      mdcp = new AnnotationMetadata(copy.getMetadata());
    else
      mdcp = new AnnotationMetadata();
    
    if (!mdcp.isSetDependencies())
      mdcp.setDependencies(new TheoryDependencies());
    mdcp.getDependencies().addToCommunicationsList(original.getUuid());
    copy.setMetadata(mdcp);
      
    this.comm = copy;
  }
  
  /**
   * Return a {@link Communication} that represents this {@link PerspectiveCommunication}.
   * 
   * It is not a pointer to the original, so changes to this communication will not propagate
   * back to this object. 
   * 
   * @return a new {@link Communication}, not a pointer, to this {@link PerspectiveCommunication}.
   */
  public Communication getPerspective() {
    return new Communication(this.comm);
  }
}
