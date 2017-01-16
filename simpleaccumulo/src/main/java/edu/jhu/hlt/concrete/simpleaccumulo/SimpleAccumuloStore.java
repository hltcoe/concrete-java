package edu.jhu.hlt.concrete.simpleaccumulo;

import java.util.Properties;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.concrete.services.store.StoreServiceWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple single-table {@link StoreCommunicationService} using a user-specified column family for isolation.
 *
 * @author travis
 */
public class SimpleAccumuloStore extends SimpleAccumulo implements StoreCommunicationService.Iface, AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(SimpleAccumuloStore.class);

  private BatchWriter writer;
  private Text colFam;
  private int numThreads;

  public SimpleAccumuloStore(SimpleAccumuloConfig config, int numThreads) {
    super(config);
    this.numThreads = numThreads;
    this.colFam = new Text(config.namespace);
  }

  @Override
  public void store(Communication c) throws ServicesException, TException {
    try {
      createTableIfNotExists();
    } catch (AccumuloException | AccumuloSecurityException e) {
      throw new ServicesException(e.getMessage());
    }
    if (writer == null) {
      // BatchWriterConfig has reasonable defaults
      BatchWriterConfig bwConfig = new BatchWriterConfig();
      bwConfig.setMaxWriteThreads(numThreads);
      try {
        writer = getConnector().createBatchWriter(config.table, bwConfig);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    Value value = new Value(commSer.serialize(c));
    Mutation mutation = new Mutation(c.getId());
    mutation.put(colFam, COMM_COL_QUALIFIER, value);
    try {
      writer.addMutation(mutation);
    } catch (MutationsRejectedException e) {
      e.printStackTrace();
      throw new ServicesException(e.getMessage());
    }
  }
  
  @Override
  public void close() throws Exception {
    if (writer != null) {
      writer.close();
      writer = null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    Properties config = SimpleAccumuloConfig.loadConfig();
    SimpleAccumuloConfig saConf = SimpleAccumuloConfig.fromConfig(config);    
    int nt = Integer.parseInt(config.getProperty("numThreads", "4"));
    int port = Integer.parseInt(config.getProperty("port", "9090"));
    logger.info("listening on port=" + port);
    logger.info("using numThreads=" + nt);
    logger.info("using " + saConf);
    try (SimpleAccumuloStore serv = new SimpleAccumuloStore(saConf, nt)) {
      serv.connect(
          config.getProperty("accumulo.user"),
          new PasswordToken(config.getProperty("accumulo.password")));  // TODO better security
      try (StoreServiceWrapper w = new StoreServiceWrapper(serv, port)) {
        w.run();
      }
    }
  }

}
