/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package concrete.tools.impl;

import java.util.ArrayList;
import java.util.List;

import concrete.tools.AnnotationDiffTool;
import concrete.tools.AnnotationException;
import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.util.ConcreteUUIDFactory;

/**
 * @author max
 *
 */
public class SingleSectionSegmenter implements AnnotationDiffTool<SectionSegmentation> {

  private final ConcreteUUIDFactory idFactory;
  
  public SingleSectionSegmenter() {
    this.idFactory = new ConcreteUUIDFactory();
  }
  
  /**
   * Generate a {@link SectionSegmentation} with a single {@link Section} that 
   * encompasses the entire text of the {@link Communication} object.
   * 
   * @param c - a {@link Communication} object to "section"
   * @return a {@link SectionSegmentation} that can be added to the {@link Communication}
   * @throws AnnotationException if there is no text in this {@link Communication}.
   */
  /*
   * (non-Javadoc)
   * @see concrete.tools.AnnotationDiffTool#annotateDiff(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public SectionSegmentation annotateDiff(Communication c) throws AnnotationException {
    if (!c.isSetText())
      throw new AnnotationException("This tool requires the communication to have text.");
    SectionSegmentation ss = new SectionSegmentation();
    // you can directly set the member...
    ss.metadata = this.getMetadata();
    
    // or use the "setter" pattern
    ss.setUuid(this.idFactory.getConcreteUUID());
    
    List<Section> sectionList = new ArrayList<Section>();
    Section s = new Section();
    
    // note that the "setter" pattern returns the object,
    // so you can chain together operations. see below.    
    s
      .setUuid(this.idFactory.getConcreteUUID())
      .setKind("Other");
    
    // you can also make use of constructors.
    // these contain all the required fields of the object.
    // below, the 'start' and 'end' are required, thus the 
    // ctor has 2 fields. 
    TextSpan ts = new TextSpan(0, c.getText().length());
    // as above, can use "getter"s to access fields
    // no need to "build" like protobufs - all done
    s.textSpan = ts;
    
    s.label = "The entire text of this Communication";
    sectionList.add(s);
    ss.sectionList = sectionList;
    
    return ss;
  }
  
  @Override
  public final AnnotationMetadata getMetadata() {
    AnnotationMetadata md = new AnnotationMetadata();
    md.setConfidence(1.0);
    md.setTimestamp(System.currentTimeMillis());
    md.setTool("Concrete Example tools v1.0");
    return md;
  }
}
