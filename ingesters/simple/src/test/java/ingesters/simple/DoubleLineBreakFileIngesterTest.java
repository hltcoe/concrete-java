package ingesters.simple;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester;
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
    sb.append(System.lineSeparator());
    sb.append(System.lineSeparator());
    sb.append("whole lotta text here");
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
    UTF8FileIngester ing = new DoubleLineBreakFileIngester("other", "other");
    Communication c = ing.fromCharacterBasedFile(tmpPath);
    List<Section> sList = c.getSectionList();
    assertEquals(testContent, c.getText());
    assertEquals(3, c.getSectionListSize());
    Section sOne = c.getSectionList().get(0);
    assertEquals("other", sOne.getKind());
    TextSpan tsOne = sOne.getTextSpan();
    assertEquals(0, tsOne.getStart());
    assertEquals(5, tsOne.getEnding());
    Section sTwo = c.getSectionList().get(1);
    TextSpan tsTwo = sTwo.getTextSpan();
    assertEquals(7, tsTwo.getStart());
    assertEquals(12, tsTwo.getEnding());
    assertEquals(testContent.length(), sList.get(2).getTextSpan().getEnding());
  }
}
