package edu.jhu.hlt.concrete.util;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CommunicationTarGzSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.TarGzCompactCommunicationSerializer;

public class TarGzAPITest {
  
  CommunicationSerializer cs = new CompactCommunicationSerializer();
  ConcreteFactory cf = new ConcreteFactory(1234);
  Communication comm1 = cf.randomCommunication();
  Communication comm2 = cf.randomCommunication();
  
  Path p1 = Paths.get("target/test1.concrete");
  Path p2 = Paths.get("target/test2.concrete");
  
  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test() throws ConcreteException {
    
  }
  
  @Test
  public void tarGzAPI () throws Exception {
    Path testPath = Paths.get("target/test-out.tar.gz");
    CommunicationTarGzSerializer ts = new TarGzCompactCommunicationSerializer();
    List<Communication> cList = new ArrayList<>();
    cList.add(comm1);
    cList.add(comm2);
    ts.toTarGz(cList, testPath);
    
    try (InputStream is = Files.newInputStream(testPath)) {
      Iterator<Communication> commIter = ts.fromTarGz(is);
      Communication comm1copy = commIter.next();
      assertEquals(comm1, comm1copy);
      Communication comm2copy = commIter.next();
      assertEquals(comm2, comm2copy);
    }
  }
}
