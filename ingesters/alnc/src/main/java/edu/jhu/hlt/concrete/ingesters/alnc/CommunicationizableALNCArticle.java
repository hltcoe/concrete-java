/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.alnc;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import edu.jhu.hlt.alnc.ALNCArticleBean;
import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.communications.Communicationizable;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * Class that represents an ALNC article that can be converted to a Concrete {@link Communication}.
 */
public class CommunicationizableALNCArticle implements Communicationizable, SafeTooledAnnotationMetadata {

  private final long ts;
  private final ALNCArticleBean bean;

  private static final DateTimeFormatter alncDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

  public CommunicationizableALNCArticle(ALNCArticleBean bean) {
    this.ts = Timing.currentLocalTime();
    this.bean = bean;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return this.ts;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolName()
   */
  @Override
  public String getToolName() {
    return ALNCIngester.class.getName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolNotes()
   */
  @Override
  public List<String> getToolNotes() {
    List<String> notes = new ArrayList<String>();
    notes.add("State: " + this.bean.getState());
    notes.add("City: " + this.bean.getCity());
    notes.add("Domain: " + this.bean.getDomain());
    notes.add("Article number: " + this.bean.getArticleNumber());
    return notes;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.communications.Communicationizable#toCommunication()
   */
  @Override
  public Communication toCommunication() throws ConcreteException {
    Communication c = new Communication();
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator g = f.create();
    c.setUuid(g.next());
    c.setId(this.bean.extractId());
    c.setText(this.bean.getContent());
    final AnnotationMetadata md = TooledMetadataConverter.convert(this);
    c.setMetadata(md);
    long millis = alncDateFormatter.parseMillis(this.bean.getDate());
    c.setStartTime(millis / 1000);
    c.setType("news");
    return c;
  }
}
