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

/**
 * Simple single-table {@link FetchCommunicationService} using a user-specified column family for isolation.
 *
 * @author travis
 */
public class SimpleAccumuloPrintIds {
  public static void main(String[] args) throws Exception {
    Properties properties = System.getProperties();
    SimpleAccumuloConfig saConf = SimpleAccumuloConfig.fromConfig(properties);
    SimpleAccumulo sa = new SimpleAccumulo(saConf);
    sa.connect(
        properties.getProperty("accumulo.user"),
        new PasswordToken(properties.getProperty("accumulo.password")));
    try (AutoCloseableIterator<Communication> it = sa.scan();) {
      while (it.hasNext()) {
        System.out.println(it.next().getId());
      }
    }
  }
}
