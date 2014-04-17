/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.SentenceSegmentationCollection;

/**
 * @author max
 *
 */
public class ValidatableSentenceSegmentationCollection extends AbstractAnnotation<SentenceSegmentationCollection> {
  
  /**
   * 
   * @param annotation
   */
  public ValidatableSentenceSegmentationCollection(SentenceSegmentationCollection annotation) {
    super(annotation);
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.rebar.annotations.AbstractRebarAnnotation#validate(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public boolean isValidWithComm(Communication c) {
    int sentCollLen = this.annotation.getSentSegListSize();

    List<SectionSegmentation> sectSegList = c.getSectionSegmentations();
    if (sectSegList != null && sectSegList.size() == sentCollLen) {
      // Map from UUID --> Section
      Map<String, Section> idToSectionSegMap = new HashMap<String, Section>(sentCollLen);
      
      for (SectionSegmentation ss : sectSegList) {
        for (Section s : ss.getSectionList())
          idToSectionSegMap.put(s.uuid, s);
        
        for (SentenceSegmentation sts : this.annotation.getSentSegList()) {
          if (!idToSectionSegMap.containsKey(sts.sectionId))
            return false;
          
        }            
      }
    }
    
    return true;
  }

  @Override
  public boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }
}
