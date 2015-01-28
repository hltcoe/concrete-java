/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Dependency;
import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.communications.TokenizedSuperCommunication;

/**
 * @author max
 *
 */
public class GetEmptyDependencyParses {

  private static final Logger logger = LoggerFactory.getLogger(GetEmptyDependencyParses.class);
  
  
  /**
   * 
   */
  private GetEmptyDependencyParses() {
    
  }

  /**
   * @param args
   * @throws IOException 
   * @throws ConcreteException 
   */
  public static void main(String[] args) throws ConcreteException, IOException {
    if (args.length != 1) {
      logger.info("Usage: {} <path-to-file-list>", GetEmptyDependencyParses.class.getSimpleName());
      System.exit(1);
    }

    String fileListString = args[0];
    Set<String> commIdSet = new HashSet<>();
    
    List<Communication> commList = CommunicationUtils.loadCommunications(fileListString);
    for (Communication c : commList) {
      TokenizedSuperCommunication tsc = new TokenizedSuperCommunication(c);
      Collection<Tokenization> tokColl = tsc.getTokenizationIdToTokenizationMap().values();
      for (Tokenization t : tokColl) {
        List<DependencyParse> depParseList = t.getDependencyParseList();
        for (DependencyParse dp : depParseList) {
          List<Dependency> dpList = dp.getDependencyList();
          if (dpList == null || dpList.isEmpty())
            commIdSet.add(c.getId());
        }
      }
    }
    
    for (String s : commIdSet)
      System.out.println(s);
  }
}
