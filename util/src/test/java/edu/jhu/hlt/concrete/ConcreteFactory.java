/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete;

import java.util.Random;

import concrete.ontology.ConcreteOntology;
import edu.jhu.hlt.concrete.util.ConcreteUUIDFactory;

/**
 * Class for generating test Concrete data.
 * 
 * @author max
 */
public class ConcreteFactory {
  
  private final Random r;
  private static final String[] COMM_TYPES = new ConcreteOntology().getValidCommunicationTypes().toArray(new String[0]);
  private static final int COMM_TYPE_SIZE = COMM_TYPES.length;

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
      .setId("corpus_" + Math.abs(this.r.nextInt()))
      .setText("Some sample text.")
      .setType(this.randomCommunicationType());

  }
  
  /**
   * Generate an {@link AnnotationMetadata} object.
   * 
   */
  public AnnotationMetadata randomMetadata() {
    return new AnnotationMetadata()
      .setConfidence(this.r.nextFloat())
      .setTimestamp(System.currentTimeMillis())
      .setTool("ConcreteFactory");
  }
}
