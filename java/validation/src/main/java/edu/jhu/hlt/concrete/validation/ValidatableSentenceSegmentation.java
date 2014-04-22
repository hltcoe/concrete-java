/**
 * 
 */
package edu.jhu.hlt.concrete.validation;

import java.util.Map;
import java.util.Set;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SentenceSegmentation;
import edu.jhu.hlt.concrete.communications.SectionedSuperCommunication;

/**
 * @author max
 *
 */
public class ValidatableSentenceSegmentation extends AbstractAnnotation<SentenceSegmentation> {

  /**
   * @param annotation
   */
  public ValidatableSentenceSegmentation(SentenceSegmentation annotation) {
    super(annotation);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValidWithComm(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  protected boolean isValidWithComm(Communication c) {
    SectionedSuperCommunication sc = new SectionedSuperCommunication(c);
    Map<String, Section> sectIdToSectMap = sc.getSectionIdToSectionMap();
    Set<String> sectIds = sectIdToSectMap.keySet();
    boolean validSectionPointer = sectIds.contains(this.annotation.getSectionId());

    return validSectionPointer;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.validation.AbstractAnnotation#isValid()
   */
  @Override
  public boolean isValid() {
    return this.validateUUID(this.annotation.getUuid())
        && this.printStatus("Metadata must be set", this.annotation.isSetMetadata())
        && this.printStatus("Metadata must be valid", new ValidatableMetadata(this.annotation.getMetadata()).isValid())
        && this.printStatus("Sentence list must be set", this.annotation.isSetSentenceList())
        && this.printStatus("Sentence list size must be > 0", this.annotation.getSentenceListSize() > 0)
        && this.printStatus("Section ID must be set", this.annotation.isSetSectionId())
        && this.validateUUID(this.annotation.getSectionId());
  }
}
