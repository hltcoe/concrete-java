/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.alnc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.jhu.hlt.alnc.ALNCArticleBean;
import edu.jhu.hlt.alnc.ALNCFileConverter;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.stream.IteratorBasedStreamIngester;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;

/**
 * Class that allows mapping of ALNC documents into Concrete {@link Communication}
 * objects.
 */
public class ALNCIngester implements IteratorBasedStreamIngester, AutoCloseable {

  private final long ts;
  private final Path path;
  private final ALNCFileConverter conv;
  
  public ALNCIngester(Path path) throws IngestException {
    this.ts = Timing.currentLocalTime();
    this.path = path;
    try {
      this.conv = new ALNCFileConverter(Files.newInputStream(this.path));
    } catch (IOException e) {
      throw new IngestException(e);
    }
  }
  
  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.Ingester#getKind()
   */
  @Override
  public String getKind() {
    return "news";
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
    return ALNCIngester.class.getSimpleName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolNotes()
   */
  @Override
  public List<String> getToolNotes() {
    List<String> notes = new ArrayList<>();
    notes.add("Original file path: " + this.path.toString());
    return notes;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  @Override
  public Iterator<Communication> iterator() throws IngestException {
    try {
      return new ALNCCommunicationIterator(this.conv);
    } catch (IOException e) {
      throw new IngestException(e);
    }
  }
  
  private class ALNCCommunicationIterator implements Iterator<Communication> {

    private final Iterator<ALNCArticleBean> iterator;
    
    private ALNCCommunicationIterator(ALNCFileConverter conv) throws IOException {
      this.iterator = conv.stream().iterator();
    }
    
    @Override
    public boolean hasNext() {
      return this.iterator.hasNext();
    }

    @Override
    public Communication next() {
      try {
        return new CommunicationizableALNCArticle(this.iterator.next()).toCommunication();
      } catch (ConcreteException e) {
        throw new RuntimeException("Error mapping documents from ALNC to Concrete.", e);
      }
    }
  }

  @Override
  public void close() throws IngestException {
    try {
      this.conv.close();
    } catch (IOException e) {
      throw new IngestException(e);
    }
  }
}
