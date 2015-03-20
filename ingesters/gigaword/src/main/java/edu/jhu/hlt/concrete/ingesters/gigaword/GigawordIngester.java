/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.stream.UTF8FileStreamIngester;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;
import gigaword.GigawordDocumentType;
import gigaword.api.GigawordDocumentConverter;
import gigaword.interfaces.GigawordDocument;
import gigaword.interfaces.TextSpan;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that is capable of converting {@link GigawordDocument} objects to Concrete
 * {@link Communication} objects. Additionally, can stream Communication objects
 * from a {@link Path} that points to a .gz file from the Gigaword corpus.
 */
public class GigawordIngester implements UTF8FileStreamIngester {

  private final long ts;
  private static final String kindsGenerated;
  
  static {
    StringBuilder sb = new StringBuilder();
    GigawordDocumentType[] gdt = GigawordDocumentType.values();
    for (int i = 0; i < gdt.length; i++) {
      sb.append(gdt[i]);
      if (i + 1 < gdt.length)
        sb.append(", ");
    }
    
    kindsGenerated = sb.toString();
  }
  
  /**
   * Get the {@link AnnotationMetadata} for the {@link GigawordIngester} class.
   * @deprecated
   */
  @Deprecated
  public static final AnnotationMetadata getMetadata() {
    return new AnnotationMetadata()
      .setTool("ConcreteGigawordDocumentFactory")
      .setTimestamp(System.currentTimeMillis());
  }

  /**
   * Default ctor.
   */
  public GigawordIngester() {
    this.ts = Timing.currentLocalTime();
  }

  /**
   * @param gd a {@link GigawordDocument} to convert
   * @return a {@link Communication} that represents the GigawordDocument
   * @throws ConcreteException
   */
  public Communication convert(GigawordDocument gd) {
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
      Section s = new Section()
        .setUuid(UUIDFactory.newUUID())
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

  @Deprecated
  public Iterator<Communication> iterator(Path pathToGigawordGZ) throws ConcreteException {
    return new ConcreteGigawordDocumentIterator(pathToGigawordGZ);
  }

  private class ConcreteGigawordDocumentIterator implements Iterator<Communication> {

    final Iterator<GigawordDocument> baseIter;

    public ConcreteGigawordDocumentIterator(Path pathToGigawordGZ) {
      this.baseIter = new GigawordDocumentConverter().iterator(pathToGigawordGZ.toString());
    }

    @Override
    public boolean hasNext() {
      return this.baseIter.hasNext();
    }

    @Override
    public Communication next() {
      try {
        return convert(this.baseIter.next());
      } catch (Exception e) {
        throw new RuntimeException("Exception during GigawordDocument -> Communication conversion.", e);
      }
    }
  }

  @Override
  public String getKind() {
    return kindsGenerated;
  }

  @Override
  public long getTimestamp() {
    return this.ts;
  }

  @Override
  public String getToolName() {
    return GigawordIngester.class.getSimpleName();
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

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.stream.UTF8FileStreamIngester#fromPath(java.nio.file.Path)
   */
  @Override
  public Iterator<Communication> fromPath(Path path) throws IngestException {
    return new ConcreteGigawordDocumentIterator(path);
  }
}
