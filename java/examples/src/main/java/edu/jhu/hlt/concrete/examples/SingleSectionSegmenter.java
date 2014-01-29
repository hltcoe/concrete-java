/**
 * 
 */
package edu.jhu.hlt.concrete.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionKind;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.TextSpan;

/**
 * @author max
 *
 */
public class SingleSectionSegmenter extends AbstractAnnotationTool 
    implements SectionSegmenter {

  /**
   * Generate a {@link SectionSegmentation} with a single {@link Section} that 
   * encompasses the entire text of the {@link Communication} object.
   * 
   * @param c a {@link Communication} object to "section"
   * @return a {@link SectionSegmentation} that can be added to the {@link Communication}
   */
  public SectionSegmentation sectionCommunication(Communication c) {
    SectionSegmentation ss = new SectionSegmentation();
    // you can directly set the member...
    ss.metadata = getMetadata();
    
    // or use the "setter" pattern
    ss.setUuid(UUID.randomUUID().toString());
    
    List<Section> sectionList = new ArrayList<Section>();
    Section s = new Section();
    
    // note that the "setter" pattern returns the object,
    // so you can chain together operations. see below.    
    s
      .setUuid(UUID.randomUUID().toString())
      .setKind(SectionKind.OTHER);
    
    // you can also make use of constructors.
    // these contain all the required fields of the object.
    // below, the 'start' and 'end' are required, thus the 
    // ctor has 2 fields. 
    TextSpan ts = new TextSpan(0, c.getText().length() - 1);
    // as above, can use "getter"s to access fields
    // no need to "build" like protobufs - all done
    s.textSpan = ts;
    
    s.label = "The entire text of this Communication";
    sectionList.add(s);
    ss.sectionList = sectionList;
    
    return ss;
  }
}
