package edu.jhu.hlt.concrete.simpleaccumulo;

import static org.junit.Assert.assertEquals;

import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.FetchRequest;
import edu.jhu.hlt.concrete.access.FetchResult;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.services.fetch.FetchServiceWrapper;
import edu.jhu.hlt.concrete.services.store.StoreServiceWrapper;

/**
 * These tests must be able to reach the accumulo servers, e.g.
 * r8n04.cm.cluster, and the tests will fail if you can't connect to them.
 *
 * @author travis
 */
public class StoreFetchTest {
  
  private final String user = "simple_accumulo_writer";
  private final String passwordStr = "writeTHEdata?!";
  private final PasswordToken password = new PasswordToken(passwordStr);
  private final String namespace = "testns";
  private final int port = 9090;
  private Communication comm;

  private SimpleAccumuloConfig config = new SimpleAccumuloConfig(
      namespace,
      SimpleAccumuloConfig.DEFAULT_TABLE,
      SimpleAccumuloConfig.DEFAULT_INSTANCE,
      SimpleAccumuloConfig.DEFAULT_ZOOKEEPERS);
  
  @Before
  public void setup() {
    comm = new Communication();
    comm.setId("testComm");
    comm.setUuid(new UUID(""));
    comm.setText("this is the text of this test comm");
    comm.setType("document");
    comm.setMetadata(new AnnotationMetadata()
        .setTimestamp(System.currentTimeMillis()/1000)
        .setTool("test tool"));
  }

  @Test
  public void storeFetch() throws Exception {

    int numThreads = 1;

    try (SimpleAccumuloStore store = new SimpleAccumuloStore(config, numThreads)) {
      store.connect(user, password);
      store.store(comm);
    }
    
    try (SimpleAccumuloFetch fetch = new SimpleAccumuloFetch(config, numThreads)) {
      fetch.connect(user, password);
      FetchRequest r = new FetchRequest();
      r.addToCommunicationIds(comm.getId());
      FetchResult res = fetch.fetch(r);
      System.out.println("[local] got back: " + res);
      assertEquals(res.getCommunicationsSize(), 1);
      assertEquals(res.getCommunications().get(0).getText(), comm.getText());
    }
    
  }

  @SuppressWarnings("deprecation")
  @Test
  public void storeFetchServers() throws Exception {
    
    // Start a Store server
    int numThreads = 1;
    try (StoreServiceWrapper r = new StoreServiceWrapper(new SimpleAccumuloStore(config, numThreads), port)) {
      Thread t = new Thread(r);
      t.run();
      Thread.sleep(500);


      // Connect to the Store server
      try (TTransport transport = new TFramedTransport(new TSocket("localhost", port), Integer.MAX_VALUE)) {
        transport.open();
        TProtocol protocol = new TCompactProtocol(transport);
        StoreCommunicationService.Iface client = new StoreCommunicationService.Client(protocol);
        client.store(comm);
      }
    
      // Stop the Store server
      t.stop();
    }
    
    // Start a fetch server
    try (FetchServiceWrapper w = new FetchServiceWrapper(new SimpleAccumuloFetch(config, numThreads), port)) {
      Thread t = new Thread(w);
      t.run();
      Thread.sleep(500);

      // Connect to the Fetch server
      try (TTransport transport = new TFramedTransport(new TSocket("localhost", port), Integer.MAX_VALUE)) {
        transport.open();
        TProtocol protocol = new TCompactProtocol(transport);
        FetchCommunicationService.Iface client = new FetchCommunicationService.Client(protocol);
        FetchRequest rq = new FetchRequest();
        rq.addToCommunicationIds(comm.getId());
        FetchResult res = client.fetch(rq);
        System.out.println("[server] got back: " + res);
        assertEquals(res.getCommunicationsSize(), 1);
        assertEquals(res.getCommunications().get(0).getText(), comm.getText());
      }

      // Stop the Fetch server
      t.stop();
    }
  }
}
