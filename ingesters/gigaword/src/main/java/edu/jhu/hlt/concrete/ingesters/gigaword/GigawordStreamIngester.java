/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.stream.IteratorBasedStreamIngester;
import edu.jhu.hlt.concrete.ingesters.base.stream.StreamBasedStreamIngester;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;

/**
 * Java wrapper around Clojure Gigaword API.
 * <br>
 * <br>
 * Provides a way to stream over Gigaword documents, an ability to convert
 * single Gigaword .sgml documents, and an ability to convert strings that
 * represent .sgml documents.
 */
public class GigawordStreamIngester implements IteratorBasedStreamIngester,
    StreamBasedStreamIngester, SafeTooledAnnotationMetadata {

  private static final Logger LOGGER = LoggerFactory.getLogger(GigawordStreamIngester.class);

  private final Path p;
  private final GigawordDocumentConverter conv;

  /**
   * Default, no-arg ctor.
   */
  public GigawordStreamIngester(Path p) {
    this.p = p;
    this.conv = new GigawordDocumentConverter();
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
    return this.getClass().getSimpleName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  public static void main (String... args) {
    if (args.length != 2) {
      LOGGER.info("This program takes 2 arguments:");
      LOGGER.info("The first is a path to an LDC SGML file. These include Gigaword documents.");
      LOGGER.info("The second is a path to the output, where the Concrete Communication will be written.");
      LOGGER.info("Usage: {} {} {}", GigawordStreamIngester.class.getName(), "/path/to/input/sgml/file", "/path/to/output/file");
      System.exit(1);
    }
  }

  @Override
  public String getKind() {
    return "news";
  }

  @Override
  public List<String> getToolNotes() {
    return new ArrayList<>();
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.stream.StreamBasedStreamIngester#stream()
   */
  @Override
  public Stream<Communication> stream() throws IngestException {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator(), Spliterator.ORDERED), false);
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.stream.IteratorBasedStreamIngester#iterator()
   */
  @Override
  public Iterator<Communication> iterator() throws IngestException {
    return this.conv.gzToStringIterator(this.p);
  }
}
