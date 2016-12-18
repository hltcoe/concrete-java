package edu.jhu.hlt.concrete.simpleaccumulo;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specifies a location to put data.
 *
 * @author travis
 */
public class SimpleAccumuloConfig implements Serializable {
  private static final Logger logger = LoggerFactory.getLogger(SimpleAccumuloConfig.class);
  private static final long serialVersionUID = 6363692365193204723L;

  // TODO support dev/production split
  public static final String DEFAULT_TABLE = "simple_accumulo_dev";
  public static final String DEFAULT_INSTANCE = "minigrid";
  public static final String DEFAULT_ZOOKEEPERS = "r8n04.cm.cluster:2181,r8n05.cm.cluster:2181,r8n06.cm.cluster:2181";

  public final String namespace;      // used as column family
  public final String table;          // e.g. HOST_TABLE
  public final String instanceName;   // e.g. "minigrid"
  public final String zookeepers;     // e.g. "r8n04.cm.cluster:2181,r8n05.cm.cluster:2181,r8n06.cm.cluster:2181"
  
  public SimpleAccumuloConfig(String namespace, String table, String instanceName, String zookeepers) {
    if (namespace == null)
      throw new IllegalArgumentException("you must provide a namespace");
    if (table == null || table.isEmpty())
      throw new IllegalArgumentException("table is null or empty: " + table);
    if (instanceName == null)
      throw new IllegalArgumentException("you must provide an instanceName");
    if (zookeepers == null || zookeepers.isEmpty())
      throw new IllegalArgumentException("zookeepers is null or empty: " + zookeepers);
    this.namespace = namespace;
    this.table = table;
    this.instanceName = instanceName;
    this.zookeepers = zookeepers;
  }
  
  @Override
  public String toString() {
    return String.format("(SimpleAccumuloConfig namespace=%s table=%s instance=%s zookeepers=%s)",
        namespace, table, instanceName, zookeepers);
  }

  /**
   * @param username e.g. "reader"
   * @param password e.g. new PasswordToken("an accumulo reader")
   */
  public Connector connect(String username, AuthenticationToken password) throws AccumuloException, AccumuloSecurityException {
    Instance inst = new ZooKeeperInstance(instanceName, zookeepers);
    Connector conn = inst.getConnector(username, password);
    return conn;
  }
  
  public static SimpleAccumuloConfig fromConfig(Properties config) {
    return new SimpleAccumuloConfig(
        config.getProperty("accumulo.namespace"),
        DEFAULT_TABLE,
        config.getProperty("accumulo.instance", DEFAULT_INSTANCE),
        config.getProperty("accumulo.zookeepers", DEFAULT_ZOOKEEPERS));
  }

  public static Properties loadConfig() throws IOException, FileNotFoundException {
	  Properties systemConfig = System.getProperties();
    String configFilePath = systemConfig.getProperty("config.file");
    if (configFilePath == null) {
      return systemConfig;
    } else {
      Properties config = new Properties();
      config.load(new FileInputStream(new File(configFilePath)));
      return config;
    }
  }
}
