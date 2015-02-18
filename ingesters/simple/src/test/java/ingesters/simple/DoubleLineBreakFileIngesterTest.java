package ingesters.simple;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.ingesters.base.FileIngester;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.ingesters.simple.DoubleLineBreakFileIngester;

public class DoubleLineBreakFileIngesterTest {

  Path tmpPath;
  String testContent;

  @Rule
  public TemporaryFolder tf = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    tmpPath = tf.getRoot().toPath().resolve("test.txt");
    StringBuilder sb = new StringBuilder();
    sb.append("hello");
    sb.append(System.lineSeparator());
    sb.append(System.lineSeparator());
    sb.append("world");
    testContent = sb.toString();
    try (FileWriterWithEncoding writer = new FileWriterWithEncoding(tmpPath.toFile(), StandardCharsets.UTF_8)) {
      writer.write(testContent);
    }
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testFromCharacterBasedFile() throws IngestException {
    FileIngester ing = new DoubleLineBreakFileIngester("other");
    Communication c = ing.fromCharacterBasedFile(tmpPath, StandardCharsets.UTF_8);
    assertEquals(testContent, c.getText());
    Section sOne = c.getSectionList().get(0);
    assertEquals("other", sOne.getKind());
    TextSpan tsOne = sOne.getTextSpan();
    assertEquals(0, tsOne.getStart());
    assertEquals(5, tsOne.getEnding());
    Section sTwo = c.getSectionList().get(1);
    TextSpan tsTwo = sTwo.getTextSpan();
    assertEquals(7, tsTwo.getStart());
    assertEquals(12, tsTwo.getEnding());
    assertEquals(testContent.length(), tsTwo.getEnding());
  }
}
