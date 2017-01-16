package edu.jhu.hlt.concrete.simpleaccumulo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.apache.accumulo.core.client.security.tokens.PasswordToken;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.access.FetchRequest;
import edu.jhu.hlt.concrete.access.FetchResult;

public class StoreFetchTest {
  
  private Communication comm;
  
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

/*
    Properties p = System.getProperties();
    if (!p.containsKey("accumulo.user"))
      throw new RuntimeException("you must provide an accumulo.user");
    if (!p.containsKey("accumulo.password"))
      throw new RuntimeException("you must provide an accumulo.password");
    String user = p.getProperty("accumulo.user");
    PasswordToken password = new PasswordToken(p.getProperty("accumulo.password"));
*/
    String user = "simple_accumulo_writer";
    PasswordToken password = new PasswordToken("writeTHEdata?!");

    SimpleAccumuloConfig config = new SimpleAccumuloConfig(
        "testns",
        SimpleAccumuloConfig.DEFAULT_TABLE,
        SimpleAccumuloConfig.DEFAULT_INSTANCE,
        SimpleAccumuloConfig.DEFAULT_ZOOKEEPERS);
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
      System.out.println("got back: " + res);
      assertEquals(res.getCommunicationsSize(), 1);
      assertEquals(res.getCommunications().get(0).getText(), comm.getText());
    }
    
  }
}
