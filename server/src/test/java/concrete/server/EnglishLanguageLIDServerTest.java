/**
 *
 */
package concrete.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.LanguageIdentification;
import edu.jhu.hlt.concrete.server.ConcreteServer;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;
import edu.jhu.hlt.concrete.annotate.AnnotateCommunicationService;
import edu.jhu.hlt.concrete.annotate.AnnotateCommunicationService.Iface;
/**
 *
 */
public class EnglishLanguageLIDServerTest extends AbstractServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(EnglishLanguageLIDServerTest.class);

  private AnnotateCommunicationService.Client localClient;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    logger.info("Preparing to start server.");
    this.srv = new ConcreteServer(new EnglishLanguageLIDDemo(), LISTEN_PORT);
    this.serviceThread = new Thread(srv);
    this.serviceThread.setDaemon(true);
    this.serviceThread.start();
    logger.info("Server started.");

    this.initializeClientFields();
    this.localClient = new AnnotateCommunicationService.Client(this.protocol);
    logger.info("client: {}", this.localClient.getInputProtocol().getScheme());
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    this.xport.close();
    this.srv.close();
  }

  @Test
  public void test() throws Exception {
    Communication c = new Communication();
    c.setId("10505_corpus_x");
    c.setUuid(UUIDFactory.newUUID());
    c.setType("Blog");
    c.setText("Hello world! Testing this out.");
    AnnotationMetadata md = new AnnotationMetadata()
      .setTool("original")
      .setTimestamp(System.currentTimeMillis());
    c.setMetadata(md);
    assertFalse(c.isSetLidList());

    Communication wLID = this.localClient.annotate(c);
    assertTrue(wLID.isSetLidList());
    for (LanguageIdentification lid : wLID.getLidList())
      logger.info("Found LID: {}", lid.toString());
  }
}
