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
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.FetchRequest;
import edu.jhu.hlt.concrete.access.FetchResult;
import edu.jhu.hlt.concrete.services.ServicesException;

/**
 * Simple single-table {@link FetchCommunicationService} using a user-specified column family for isolation.
 *
 * @author travis
 */
public class SimpleAccumuloFetch extends SimpleAccumulo implements FetchCommunicationService.Iface, AutoCloseable {

  private Scanner reader;
  private BatchScanner readerB;
  private int numThreads;
  
  public SimpleAccumuloFetch(SimpleAccumuloConfig config, int numThreads) {
    super(config);
    this.numThreads = numThreads;
  }
  
  @Override
  public FetchResult fetch(FetchRequest fr) throws ServicesException, TException {
    if (fr == null || fr.isSetCommunicationIds() || fr.getCommunicationIdsSize() == 0)
      throw new ServicesException("no comm ids");
    
    int n = fr.getCommunicationIdsSize();
    FetchResult r = new FetchResult();
    r.setCommunications(new ArrayList<>(n));

    Authorizations auths = new Authorizations(fr.getAuths());

    try {
      if (n == 1) {
        if (reader == null)
          reader = getConnector().createScanner(config.table, auths);
        reader.setRange(Range.exact(fr.getCommunicationIds().get(0)));
        Entry<Key, Value> e = reader.iterator().next();
        byte[] commBytes = e.getValue().get();
        Communication c = new Communication();
        commDeser.deserialize(c, commBytes);
        r.addToCommunications(c);
      } else {
        if (readerB == null)
          readerB = getConnector().createBatchScanner(config.table, auths, numThreads);
        List<Range> ids = new ArrayList<>(n);
        for (String c : fr.getCommunicationIds())
          ids.add(Range.exact(c));
        readerB.setRanges(ids);
        for (Entry<Key, Value> e : readerB) {
          byte[] bytes = e.getValue().get();
          Communication c = new Communication();
          commDeser.deserialize(c, bytes);
          r.addToCommunications(c);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServicesException(e.getMessage());
    }

    return r;
  }
  
  @Override
  public void close() throws Exception {
    if (reader != null) {
      reader.close();
      reader = null;
    }
    if (readerB != null) {
      readerB.close();
      readerB = null;
    }
  }
  
  public static void main(String[] args) throws Exception {
	Properties config = System.getProperties();
    SimpleAccumuloConfig saConf = SimpleAccumuloConfig.fromConfig(config);
    int nt = Integer.parseInt(config.getProperty("numThreads", "4"));
    int port = Integer.parseInt(config.getProperty("port", "9090"));
    System.err.println("listening on port=" + port);
    System.err.println("using numThreads=" + nt);
    System.err.println("using " + saConf);
    try (SimpleAccumuloFetch serv = new SimpleAccumuloFetch(saConf, nt)) {
      serv.connect(
          config.getProperty("accumulo.user"),
          new PasswordToken(config.getProperty("accumulo.password")));  // TODO better security
      
      TServerTransport serverTransport = new TServerSocket(port);
      TServer server = new TSimpleServer(new Args(serverTransport)
          .processor(new FetchCommunicationService.Processor<>(serv)));

      // Use this for a multithreaded server
      // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

      System.out.println("Starting the simple server...");
      server.serve();
    }
  }
}
