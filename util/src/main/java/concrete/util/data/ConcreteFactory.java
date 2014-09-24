/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package concrete.util.data;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import concrete.ontology.ConcreteOntology;
import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.communications.SuperCommunication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ConcreteUUIDFactory;

/**
 * Class for generating test Concrete data.
 * 
 * @author max
 */
public class ConcreteFactory {
  
  private final Random r;
  
  private static final String[] COMM_TYPES = new ConcreteOntology()
    .getValidCommunicationTypes()
    .toArray(new String[0]);
  private static final int COMM_TYPE_SIZE = COMM_TYPES.length;
  private static final Logger logger = LoggerFactory.getLogger(ConcreteFactory.class);

  /**
   * 
   */
  public ConcreteFactory() {
    this.r = new Random();
  }
  
  public ConcreteFactory(long seed) {
    this.r = new Random(seed); 
  }
  
  public String randomCommunicationType() {
    return COMM_TYPES[this.r.nextInt(COMM_TYPE_SIZE)];
  }

  /**
   * Generate a {@link Communication} object with basic fields set.
   */
  public Communication randomCommunication() {
    return new Communication()
      .setUuid(new ConcreteUUIDFactory().getConcreteUUID())
      .setId("corpus_" + this.r.nextInt(Integer.MAX_VALUE))
      .setText("Some sample text.")
      .setType(this.randomCommunicationType())
      .setMetadata(this.randomMetadata());
  }
  
  /**
   * Generate an {@link AnnotationMetadata} object.
   * 
   */
  public AnnotationMetadata randomMetadata() {
    return new AnnotationMetadata()
      .setTimestamp(System.currentTimeMillis())
      .setTool("ConcreteFactory");
  }
  
  public static void main (String... args) throws ConcreteException {
    if (args.length < 1 || args.length > 2) {
      logger.info("Usage: {} <path-to-output-file>", ConcreteFactory.class.getName());
      logger.info("Optional argument: <delete-if-exists>");
      logger.info("e.g., my/output/folder.concrete true");
      System.exit(1);
    }
    
    boolean overWrite = true;
    if (args.length == 2)
      overWrite = Boolean.parseBoolean(args[1]);    
    new SuperCommunication(new ConcreteFactory().randomCommunication()).writeToFile(args[0], overWrite);
  }
}
