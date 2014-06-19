/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import java.util.HashMap;
import java.util.Iterator;
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
  protected boolean isValidWithComm(Communication c) {
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
    boolean initValidation = 
        this.printStatus("Metadata must be set", this.annotation.isSetMetadata())
        && this.printStatus("Metadata must be valid", new ValidatableMetadata(this.annotation.getMetadata()).isValid())
        // Sections must exist.
        && this.printStatus("Sections must exist", this.annotation.isSetSentSegList())
        // Section size must be >0.
        && this.printStatus("Section size must be >0", this.annotation.getSentSegListSize() > 0);
    if (initValidation) {
      Iterator<SentenceSegmentation> iter = this.annotation.getSentSegListIterator();
      boolean subValid = true;
      while (subValid && iter.hasNext())
        subValid = new ValidatableSentenceSegmentation(iter.next()).isValid();
      
      return subValid;
    } else
      return false;
  }
}
