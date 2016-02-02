/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.streams;

import java.io.IOException;
import java.util.Random;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * Produces {@link Communication}s in a stream. Useful for testing
 * consumers via e.g. named pipes.
 */
public class RandomCommunicationStreamProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(RandomCommunicationStreamProducer.class);

  /**
   * @param args
   * @throws ConcreteException
   * @throws IOException
   * @throws TException
   */
  public static void main(String[] args) throws ConcreteException, IOException, TException {
    Random r = new Random();

    CompactCommunicationSerializer cs = new CompactCommunicationSerializer();
    Communication root = new Communication();
    root.setType("tweet");
    AnnotationMetadata md = new AnnotationMetadata();
    md.setTool(ProjectConstants.PROJECT_NAME + " " + ProjectConstants.VERSION + " " + RandomCommunicationStreamProducer.class.getSimpleName());
    md.setTimestamp(Timing.currentLocalTime());
    root.setMetadata(md);
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator g = f.create();
    while (true) {
      Communication nc = new Communication(root);
      String idstr = "communication" + Math.abs(r.nextInt());
      nc.setId(idstr);
      nc.setUuid(g.next());
      nc.setText("typical twitter stuff");
      System.out.write(cs.toBytes(nc));
    }
  }
}
