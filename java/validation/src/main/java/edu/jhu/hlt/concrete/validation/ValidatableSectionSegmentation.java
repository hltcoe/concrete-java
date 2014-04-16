/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import java.util.Iterator;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.util.Util;

/**
 * @author max
 *
 */
public class ValidatableSectionSegmentation extends AbstractAnnotation<SectionSegmentation> {

  /**
   * 
   */
  public ValidatableSectionSegmentation(SectionSegmentation annot) {
    super(annot);
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.rebar.ballast.validation.AbstractAnnotation#isValid(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public boolean isValid(Communication c) {
    boolean valid =
        // Rebar depends upon valid metadata.
        this.annotation.isSetMetadata()
        // UUID string must be a UUID string.
        && Util.isValidUUIDString(this.annotation.uuid)
        // Sections must exist.
        && this.annotation.isSetSectionList()
        // Section size must be >1.
        && this.annotation.getSectionListSize() > 0;
    Iterator<Section> sects = this.annotation.getSectionListIterator();
    while (valid && sects.hasNext()) {
      Section s = sects.next();
      valid = Util.isValidUUIDString(s.uuid)
          && new ValidatableTextSpan(s.getTextSpan()).isValid(c);
    }
    
    return valid;
  }

  @Override
  public boolean isValid() {
    // TODO Auto-generated method stub
    return false;
  }
}
