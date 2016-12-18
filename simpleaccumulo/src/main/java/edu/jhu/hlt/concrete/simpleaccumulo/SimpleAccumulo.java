package edu.jhu.hlt.concrete.simpleaccumulo;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.admin.TableOperations;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TCompactProtocol;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.services.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Super-class for this module's server implementations.
 *
 * @author travis
 */
public class SimpleAccumulo {
  private static final Logger logger = LoggerFactory.getLogger(SimpleAccumulo.class);

  // Rows are communication ids, column family is a user provided namespace,
  // and this column qualifier is the address of the comm bytes.
  public static final Text COMM_COL_QUALIFIER = new Text("comm_bytes");

  // How (Communication) values are stored
  public static final TCompactProtocol.Factory COMM_SERIALIZATION_PROTOCOL = new TCompactProtocol.Factory();

  // Configuration
  protected ServiceInfo about;
  protected SimpleAccumuloConfig config;
  
  // State
  private Connector conn;
  
  protected TSerializer commSer;
  protected TDeserializer commDeser;
  
  public SimpleAccumulo(SimpleAccumuloConfig config) {
    this.config = config;
    this.about = new ServiceInfo()
        .setName(this.getClass().getName())
        .setDescription("a minimal accumulo-backed concrete-services implementation")
        .setVersion("0.1");
    this.commSer = new TSerializer(COMM_SERIALIZATION_PROTOCOL);
    this.commDeser = new TDeserializer(COMM_SERIALIZATION_PROTOCOL);
  }

  /**
   * You must call this before any reading/writing method.
   *
   * @param username e.g. "reader"
   * @param password e.g. new PasswordToken("an accumulo reader")
   */
  public Connector connect(String username, AuthenticationToken password) throws Exception {
    logger.info("connecting to=" + config + " with username=" + username);
    conn = config.connect(username, password);  
    return conn;
  }
  
  public Connector getConnector() {
    if (conn == null)
      throw new IllegalStateException("you must call connect first!");
    return conn;
  }
  
  // More of a demo than a useful method
  public AutoCloseableIterator<Communication> scan() throws TableNotFoundException {
    return scan(null);
  }
  public AutoCloseableIterator<Communication> scan(Range rows) throws TableNotFoundException {
    Scanner s = getConnector().createScanner(config.table, new Authorizations());

    // Restrict to key-values matching the given namespace, only the comm bytes
    s.fetchColumn(new Text(config.namespace), COMM_COL_QUALIFIER);

    if (rows != null)
      s.setRange(rows);

    return new AutoCloseableIterator<Communication>() {
      Iterator<Entry<Key, Value>> entries = s.iterator();
      @Override
      public void close() throws Exception {
        s.close();
      }
      @Override
      public boolean hasNext() {
        return entries.hasNext();
      }
      @Override
      public Communication next() {
        Communication c = new Communication();
        Entry<Key, Value> e = entries.next();
        byte[] bytes = e.getValue().get();
        try {
          commDeser.deserialize(c, bytes);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
        return c;
      }
    };
  }

  public ServiceInfo about() throws TException {
    return about;
  }

  public boolean alive() throws TException {
    return true;
  }

  public void createTableIfNotExists() throws AccumuloException, AccumuloSecurityException {
    TableOperations tableOps = getConnector().tableOperations();
    try {
      if (! tableOps.exists(config.table)) {
        tableOps.create(config.table);
      }
    } catch (TableExistsException e) {
      logger.warn("table came into existence between calls: {}",
        e.getMessage());
    }
  }
}
