package edu.jhu.hlt.concrete.zip;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;

public class ConcreteZipIOTest {

  String p = "src/test/resources/simple.zip";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testOpenAsMap() {
    Map<String, Communication> comms = ConcreteZipIO.openAsMap(p);
    assertEquals("TEST", comms.get("simple_1").getType());
    assertEquals("TEST", comms.get("simple_2").getType());
    assertEquals("TEST", comms.get("simple_3").getType());
  }

  @Test
  public void testRead() {
    Iterable<Communication> comms = ConcreteZipIO.read(p);
    for (Communication comm : comms) {
      assertEquals("TEST", comm.getType());
    }
  }

  @Test
  public void testReadAsStream() {
    Stream<Communication> stream = ConcreteZipIO.readAsStream(p);
    stream.forEach(comm -> {
      assertEquals("TEST", comm.getType());
    });
  }
}
