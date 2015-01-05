/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.serialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public class PrintConcreteTarGzArchive {
  
  private static final Logger logger = LoggerFactory.getLogger(PrintConcreteTarGzArchive.class);
  
  /**
   * 
   */
  public PrintConcreteTarGzArchive() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param args
   * @throws IOException 
   * @throws ConcreteException 
   */
  public static void main(String[] args) throws ConcreteException, IOException {
    if (args.length != 1) {
      logger.info("Usage: {} {}", PrintConcreteTarGzArchive.class.getName(), "path/to/tar/gz/file");
      System.exit(1);
    }
    
    CommunicationTarGzSerializer ser = new ThreadSafeTarGzCompactCommunicationSerializer();
    InputStream is = new FileInputStream(args[0]);
    Iterator<Communication> ci = ser.fromTarGz(is);
    while (ci.hasNext()) {
      Communication c = ci.next();
      logger.info("Archive contains document: {}", c.getId());
    }
    
    is.close();
  }
}
