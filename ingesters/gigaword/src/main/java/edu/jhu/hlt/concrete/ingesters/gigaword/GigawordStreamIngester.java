/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.stream.IteratorBasedStreamIngester;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import gigaword.GigawordDocumentType;
import gigaword.api.GigawordDocumentConverter;
import gigaword.interfaces.GigawordDocument;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that is capable of converting {@link GigawordDocument} objects to Concrete
 * {@link Communication} objects. Additionally, can stream Communication objects
 * from a {@link Path} that points to a .gz file from the Gigaword corpus.
 */
public class GigawordStreamIngester implements IteratorBasedStreamIngester {

  private final Path path;

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
   * Default ctor.
   */
  public GigawordStreamIngester(Path path) {
    this.path = path;
    this.ts = Timing.currentLocalTime();
  }

  private static class ConcreteGigawordDocumentIterator implements Iterator<Communication> {

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
        return new CommunicationizableGigawordDocument(this.baseIter.next()).toCommunication();
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
    return GigawordStreamIngester.class.getSimpleName();
  }

  @Override
  public List<String> getToolNotes() {
    List<String> notes = new ArrayList<>();
    notes.add("Original document path: " + this.path.toString());
    return notes;
  }

  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  @Override
  public Iterator<Communication> iterator() throws IngestException {
    return new ConcreteGigawordDocumentIterator(path);
  }
}
