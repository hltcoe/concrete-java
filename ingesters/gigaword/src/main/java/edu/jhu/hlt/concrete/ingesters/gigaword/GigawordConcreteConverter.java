/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.gigaword;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.serialization.CommunicationTarGzSerializer;
import edu.jhu.hlt.concrete.serialization.TarGzCompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 *
 */
public class GigawordConcreteConverter {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(GigawordConcreteConverter.class);

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      LOGGER.info("Usage: {} {} {}", GigawordConcreteConverter.class.getName(), 
          "path/to/gigaword/gz/file.gz", "path/to/out/file.tar.gz");
      System.exit(1);
    }
    
    String gzPathStr = args[0];
    String outPathStr = args[1];

    CommunicationTarGzSerializer ser = new TarGzCompactCommunicationSerializer();
    GigawordStreamIngester factory = new GigawordStreamIngester(Paths.get(gzPathStr));

    try {
      Iterator<Communication> iter = factory.iterator();
      Set<Communication> cSet = new HashSet<>(10000);
      while (iter.hasNext()) 
        cSet.add(iter.next());
      
      ser.toTarGz(cSet, Paths.get(outPathStr));
    } catch (ConcreteException | IOException | IngestException e) {
      LOGGER.error("Caught exception while performing the mapping.", e);
    }
  }
}
