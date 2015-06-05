/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.CommunicationFactory;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.section.SectionFactory;
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

  private final IFn gzPathToGigaStringIterFx;

  private final Keyword bkw;
  private final Keyword ekw;
  private final Keyword tkw;

  /**
   * Default, no-arg ctor.
   */
  public GigawordDocumentConverter() {
    IFn req = Clojure.var("clojure.core", "require");
    req.invoke(Clojure.read("gigaword.core"));

    this.gzPathToGigaStringIterFx = Clojure.var("gigaword.core", "process-ldc-sgml");

    this.bkw = Keyword.find("b");
    this.ekw = Keyword.find("e");
    this.tkw = Keyword.find("t");
  }

  private Section fromPAM(final PersistentArrayMap pam) {
    LOGGER.debug("Running on PAM: {}", pam.toString());
    Section s = SectionFactory.create();
    LOGGER.debug("B keyword: {}", this.bkw);
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
    PersistentArrayMap map = (PersistentArrayMap) this.gzPathToGigaStringIterFx.invoke(ldcSgml);
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
   * @return a {@link GigawordDocument} object that represents the .sgml file
   */
  public Communication fromPath(String pathToGigaSGMLFile) {
    return this.fromSgmlString(pathToGigaSGMLFile);
  }

  /**
   * @param pathToGigaSGMLFile a {@link Path} that represents a path to a .sgml file on disk.
   * @return a {@link GigawordDocument} object that represents the .sgml file
   */
  public Communication fromPath(Path pathToGigaSGMLFile) {
    return this.fromSgmlString(pathToGigaSGMLFile.toString());
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
}
