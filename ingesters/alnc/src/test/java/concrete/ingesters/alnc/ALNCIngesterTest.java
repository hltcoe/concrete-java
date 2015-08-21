package concrete.ingesters.alnc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.alnc.ALNCIngester;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;

public class ALNCIngesterTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ALNCIngesterTest.class);

  Path p = Paths.get("src/test/resources/fake.json");

  @Test
  public void testIterator() throws IngestException {
    try (ALNCIngester ing = new ALNCIngester(p);) {
      Iterator<Communication> iter = ing.iterator();
      int ct = 0;
      while (iter.hasNext()) {
        Communication c = iter.next();
        LOGGER.info("ID: {}", c.getId());
        LOGGER.info("UUID: {}", c.getUuid());
        AnnotationMetadata md = c.getMetadata();
        LOGGER.info("Got md: {}", md.toString());
        assertTrue(md.getTool().contains("ALNC"));
        LOGGER.info("Got text: {}", c.getText());
        assertEquals("news", c.getType());
        ct++;
      }

      assertEquals(2, ct);
    }
  }
}
