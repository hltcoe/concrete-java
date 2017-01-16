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
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.access.StoreCommunicationService.Processor;
import edu.jhu.hlt.concrete.services.ServicesException;

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

      Processor<SimpleAccumuloStore> proc = 
          new StoreCommunicationService.Processor<>(serv);

      TNonblockingServerTransport transport = new TNonblockingServerSocket(port);
      TNonblockingServer.Args serverArgs = new TNonblockingServer.Args(transport);
      serverArgs = serverArgs.processorFactory(new TProcessorFactory(proc));
      serverArgs = serverArgs.protocolFactory(new TCompactProtocol.Factory());
      serverArgs = serverArgs.transportFactory(new TFramedTransport.Factory(Integer.MAX_VALUE));
      serverArgs.maxReadBufferBytes = Long.MAX_VALUE;
      TNonblockingServer server = new TNonblockingServer(serverArgs);
      
      /*
      TServerTransport serverTransport = new TServerSocket(port);
      TServer server = new TSimpleServer(new Args(serverTransport)
          .processor(new StoreCommunicationService.Processor<>(serv)));
      */

      logger.info("Starting the simple server...");
      server.serve();
    }
  }

}
