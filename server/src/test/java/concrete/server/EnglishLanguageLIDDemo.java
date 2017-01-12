/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package concrete.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.LanguageIdentification;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.annotate.AnnotateCommunicationService;
import edu.jhu.hlt.concrete.annotate.AnnotateCommunicationService.Iface;
import edu.jhu.hlt.concrete.services.ConcreteThriftException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * Example implementation of a Concrete Thrift analytic.
 * <br>
 * <br>
 * This analytic simply appends a {@link LanguageIdentification} object
 * with contents: <pre>"eng" : 1.0d</pre> to the {@link Communication}'s LID list.
 */
public class EnglishLanguageLIDDemo implements AnnotateCommunicationService.Iface, SafeTooledAnnotationMetadata {

  private static final String demoStr = "This tool adds an English LID annotation to the communication.";

  /**
   *
   */
  public EnglishLanguageLIDDemo() {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.services.AnnotateCommunicationService.Iface#annotate(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public Communication annotate(Communication original) throws ConcreteThriftException, TException {
    LanguageIdentification lid = new LanguageIdentification();
    lid.setUuid(UUIDFactory.newUUID());
    Map<String, Double> langToProbMap = new HashMap<>();
    langToProbMap.put("eng", 1.0d);
    lid.setLanguageToProbabilityMap(langToProbMap);
    lid.setMetadata(this.getMetadata());

    Communication nc = new Communication(original);
    nc.addToLidList(lid);
    return nc;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.services.AnnotateCommunicationService.Iface#getMetadata()
   */
  @Override
  public AnnotationMetadata getMetadata() throws TException {
    return TooledMetadataConverter.convert(this);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.services.AnnotateCommunicationService.Iface#getDocumentation()
   */
  @Override
  public String getDocumentation() throws TException {
    return demoStr;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.services.AnnotateCommunicationService.Iface#shutdown()
   */
  @Override
  public void shutdown() throws TException {

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
    return EnglishLanguageLIDDemo.class.getName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }
}
