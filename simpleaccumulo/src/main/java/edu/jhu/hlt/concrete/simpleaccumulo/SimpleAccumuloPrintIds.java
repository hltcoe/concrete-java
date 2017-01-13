package edu.jhu.hlt.concrete.simpleaccumulo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

import edu.jhu.hlt.concrete.Communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleAccumuloPrintIds {
  private static final Logger logger = LoggerFactory.getLogger(SimpleAccumuloPrintIds.class);
  public static void main(String[] args) throws Exception {
    Properties config = SimpleAccumuloConfig.loadConfig();
    SimpleAccumuloConfig saConf = SimpleAccumuloConfig.fromConfig(config);
    SimpleAccumulo sa = new SimpleAccumulo(saConf);
    sa.connect(
        config.getProperty("accumulo.user"),
        new PasswordToken(config.getProperty("accumulo.password")));
    long numComms = 0;
    try (AutoCloseableIterator<Communication> it = sa.scan();) {
      while (it.hasNext()) {
        System.out.println(it.next().getId());
        ++numComms;
      }
    }
    logger.info("Scanned {} communications", numComms);
  }
}
