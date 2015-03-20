/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.ingesters.base.communications.Communicationizable;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;
import gigaword.GigawordDocumentType;
import gigaword.interfaces.GigawordDocument;
import gigaword.interfaces.TextSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class that represents a {@link GigawordDocument} that can be converted to a Concrete
 * {@link Communication}.
 */
public class CommunicationizableGigawordDocument implements GigawordDocument, Communicationizable, SafeTooledAnnotationMetadata {

  private final GigawordDocument gd;
  private final long ts;
  
  public CommunicationizableGigawordDocument(GigawordDocument gd) {
    this.gd = gd;
    this.ts = Timing.currentLocalTime();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see gigaword.interfaces.GigawordDocument#getText()
   */
  @Override
  public String getText() {
    return this.gd.getText();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gigaword.interfaces.GigawordDocument#getId()
   */
  @Override
  public String getId() {
    return this.gd.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gigaword.interfaces.GigawordDocument#getTextSpans()
   */
  @Override
  public List<TextSpan> getTextSpans() {
    return this.gd.getTextSpans();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gigaword.interfaces.GigawordDocument#getHeadline()
   */
  @Override
  public Optional<String> getHeadline() {
    return this.gd.getHeadline();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gigaword.interfaces.GigawordDocument#getDateline()
   */
  @Override
  public Optional<String> getDateline() {
    return this.gd.getDateline();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gigaword.interfaces.GigawordDocument#getMillis()
   */
  @Override
  public long getMillis() {
    return this.gd.getMillis();
  }

  /*
   * (non-Javadoc)
   * 
   * @see gigaword.interfaces.GigawordDocument#getType()
   */
  @Override
  public GigawordDocumentType getType() {
    return this.gd.getType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.jhu.hlt.concrete.ingesters.base.communications.Communicationizable#toCommunication()
   */
  @Override
  public Communication toCommunication() throws ConcreteException {
    Communication c = new Communication()
        .setUuid(UUIDFactory.newUUID())
        .setId(gd.getId())
        .setStartTime(gd.getMillis() / 1000)
        .setType(gd.getType().toString())
        .setText(gd.getText())
        .setMetadata(TooledMetadataConverter.convert(this));

    List<Section> sectList = new ArrayList<Section>();
    int nCtr = 0;
    for (TextSpan ts : gd.getTextSpans()) {
      Section s = new Section().setUuid(UUIDFactory.newUUID())
          .setKind("Passage")
          .setTextSpan(new edu.jhu.hlt.concrete.TextSpan(ts.getStart(), ts.getEnding()));
      s.addToNumberList(nCtr);
      nCtr += 1;
      sectList.add(s);
    }

    boolean hasHeadline = gd.getHeadline().isPresent();
    boolean hasDateline = gd.getDateline().isPresent();

    // Headline + dateline --> Section 1 == Title, Section 2 == Dateline
    if (hasHeadline && hasDateline) {
      sectList.get(0).setKind("Title");
      sectList.get(1).setKind("Dateline");
      // Only headline --> Section 1 == Title
    } else if (hasHeadline && !hasDateline)
      sectList.get(0).setKind("Title");
    // Only dateline --> Section 1 == Dateline
    else if (!hasHeadline && hasDateline)
      sectList.get(0).setKind("Dateline");

    c.setSectionList(sectList);
    return c;
  }

  @Override
  public long getTimestamp() {
    return this.ts;
  }

  @Override
  public String getToolName() {
    return CommunicationizableGigawordDocument.class.getName() + " [Project: " + ProjectConstants.PROJECT_NAME + "]";
  }

  @Override
  public List<String> getToolNotes() {
    List<String> notes = new ArrayList<>();
    notes.add("If the document contains a title, the first section will be the title.");
    notes.add("If the document contains a title and a headline, the second section will be the headline.");
    notes.add("If the document contains no title and a headline, the first section will be the headline.");
    return notes;
  }

  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }
}
