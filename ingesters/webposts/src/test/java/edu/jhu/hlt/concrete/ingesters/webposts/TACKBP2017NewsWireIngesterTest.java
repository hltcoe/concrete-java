package edu.jhu.hlt.concrete.ingesters.webposts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;

public class TACKBP2017NewsWireIngesterTest {

  static final Path p = Paths.get("src/test/resources/dog-bites-man.xml");

  static final String[] passageContents = {"Dog Bites Man",
      "John Smith, manager of ACME INC, was bit by a dog on March 10th, 2013.",
      "He died!",
      "John's daughter Mary expressed sorrow.",
  };

  @Test
  public void dateFromID() throws IngestException {
    String example = "NYT_ENG_20131231.0085";
    long extracted = new TACKBP2017NewsWireIngester().inferStartTimeFromID(example);
    assertEquals(1388448000L, extracted);
  }

  @Test
  public void documentIngest() throws IngestException {
    Communication c = new TACKBP2017NewsWireIngester().fromCharacterBasedFile(p);
    assertEquals("ABC_ENG_20010113.0000", c.getId());
    assertEquals(4, c.getSectionListSize());
    assertTrue(c.isSetText());
    List<Section> slist = c.getSectionList();
    for (int i = 0; i < 4; i++) {
      Section s = slist.get(i);
      assertTrue(s.isSetTextSpan());
      TextSpan ts = s.getTextSpan();
      String sectText = c.getText().substring(ts.getStart(), ts.getEnding());
      assertEquals(passageContents[i], sectText);
    }
  }
}
