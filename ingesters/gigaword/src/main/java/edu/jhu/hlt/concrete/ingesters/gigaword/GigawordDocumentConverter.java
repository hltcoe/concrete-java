/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentVector;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.CommunicationFactory;
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;
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
public class GigawordDocumentConverter implements SafeTooledAnnotationMetadata {

  private static final Logger LOGGER = LoggerFactory.getLogger(GigawordDocumentConverter.class);

  private final IFn processSgmlFx;
  private final IFn gzToStringListFx;

  private final Keyword bkw;
  private final Keyword ekw;
  private final Keyword tkw;

  /**
   * Default, no-arg ctor.
   */
  public GigawordDocumentConverter() {
    IFn req = Clojure.var("clojure.core", "require");
    req.invoke(Clojure.read("gigaword.core"));

    this.processSgmlFx = Clojure.var("gigaword.core", "process-ldc-sgml");
    this.gzToStringListFx = Clojure.var("gigaword.core", "gz->docs");

    this.bkw = Keyword.find("b");
    this.ekw = Keyword.find("e");
    this.tkw = Keyword.find("t");
  }

  private final class LocalCommIterator implements Iterator<Communication> {

    // really Iterator<String>, cast per use
    private final Iterator<?> iter;

    /**
     *
     */
    public LocalCommIterator(PersistentVector seq) {
      this.iter = seq.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      return this.iter.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public Communication next() {
      String sgml = (String) this.iter.next();
      return fromSgmlString(sgml);
    }
  }

  public Iterator<Communication> gzToStringIterator(Path gzPath) {
    String pathStr = gzPath.toAbsolutePath().toString();
    PersistentVector seq = (PersistentVector)this.gzToStringListFx.invoke(pathStr);
    return new LocalCommIterator(seq);
  }

  private Section fromPAM(final PersistentArrayMap pam) {
    LOGGER.debug("Running on PAM: {}", pam.toString());
    Section s = SectionFactory.create();
    final long begin = (long) pam.get(this.bkw);
    final int bi = (int)begin;
    final long end = (long) pam.get(this.ekw);
    final int ei = (int)end;
    final Keyword kind = (Keyword) pam.get(this.tkw);
    TextSpan ts = new TextSpan(bi, ei);
    s.setKind(kind.sym.getName());
    s.setTextSpan(ts);
    return s;
  }

  public Communication fromSgmlString(String ldcSgml) {
    PersistentArrayMap map = (PersistentArrayMap) this.processSgmlFx.invoke(ldcSgml);
    String kind = (String) map.get(Keyword.find("kind"));
    String id = (String) map.get(Keyword.find("id"));
    long date = (long)map.get(Keyword.find("date"));

    Communication c = CommunicationFactory.create()
        .setId(id)
        .setStartTime(date / 1000)
        .setType(kind)
        .setText(ldcSgml)
        .setMetadata(TooledMetadataConverter.convert(this));

    Keyword skw = Keyword.find("sections");
    @SuppressWarnings("unchecked")
    List<PersistentArrayMap> sectionList = (List<PersistentArrayMap>) map.get(skw);
    LOGGER.debug("# sects: {}", sectionList.size());
    sectionList.forEach(pam -> c.addToSectionList(this.fromPAM(pam)));
    return c;
  }

  /**
   * @param pathToGigaSGMLFile a string that represents a path to a .sgml file on disk.
   * @return a {@link Communication} object that represents the .sgml file
   */
  public Communication fromPath(String pathToGigaSGMLFile) throws IOException {
    return this.fromPath(Paths.get(pathToGigaSGMLFile));
  }

  /**
   * @param pathToGigaSGMLFile a {@link Path} that represents a path to a .sgml file on disk.
   * @return a {@link Communication} object that represents the .sgml file
   */
  public Communication fromPath(Path pathToGigaSGMLFile) throws IOException {
    try(InputStream is = Files.newInputStream(pathToGigaSGMLFile);
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 24);) {
      return this.fromSgmlString(IOUtils.toString(bin));
    }
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
      LOGGER.info("Usage: {} {} {}", GigawordDocumentConverter.class.getName(), "/path/to/input/sgml/file", "/path/to/output/file");
      System.exit(1);
    }

    String pathStr = args[0];
    String outStr = args[1];

    Path path = Paths.get(pathStr);
    if(!Files.exists(path)) {
      LOGGER.error("Path {} does not exist.", path.toString());
      System.exit(1);
    }

    try {
      Communication c = new GigawordDocumentConverter().fromPath(path);
      new WritableCommunication(c).writeToFile(Paths.get(outStr), true);
    } catch (ConcreteException | IOException e) {
      LOGGER.error("Caught Exception during conversion.", e);
    }
  }
}
