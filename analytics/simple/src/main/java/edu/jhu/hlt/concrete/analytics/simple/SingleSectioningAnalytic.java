/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.analytics.simple;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.analytics.base.Analytic;
import edu.jhu.hlt.concrete.analytics.base.AnalyticException;
import edu.jhu.hlt.concrete.analytics.base.DependentAnalytic;
import edu.jhu.hlt.concrete.section.SingleSectionSegmenter;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;

/**
 * Implementation of {@link Analytic} that takes a {@link Communication}
 * object without any {@link Section}s and creates a single section
 * that encompasses the entire text.
 */
public class SingleSectioningAnalytic implements DependentAnalytic {
  
  private final String sectionKinds;
  
  /**
   * @param sectionKinds the kinds of {@link Section} objects to produce
   */
  public SingleSectioningAnalytic(final String sectionKinds) {
    this.sectionKinds = sectionKinds;
  }
  
  /**
   * Create {@link Section} objects with kind set to 'Other'.
   */
  public SingleSectioningAnalytic() {
    this("Other");
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
    return SingleSectioningAnalytic.class.getSimpleName();
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
    return new ArrayList<String>();
  }
  
  @Override
  public boolean isAnnotatable(Communication c) {
    boolean hasSections = c.isSetSectionList() && c.getSectionListSize() > 0;
    boolean valid = hasSections && c.isSetText() && !c.getText().equals("");
    return valid;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.analytics.base.Analytic#annotate(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public Communication annotate(Communication c) throws AnalyticException {
    // ensure this analytic can process this communication
    if (!this.isAnnotatable(c))
      throw new AnalyticException("Communication has sections already, or it does not have text.");
    try {
      Communication cpy = new Communication(c);
      Section s = SingleSectionSegmenter.createSingleSection(cpy, this.sectionKinds);
      cpy.addToSectionList(s);
      return cpy;
    } catch (ConcreteException e) {
      // will not throw - inputs have been checked
      throw new AnalyticException(e);
    }
  }
}
