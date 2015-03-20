/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package concrete.ingesters.gigaword;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.communications.SuperCommunication;
import edu.jhu.hlt.concrete.ingesters.gigaword.CommunicationizableGigawordDocumentImpl;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.SuperTextSpan;
import gigaword.api.GigawordDocumentConverter;
import gigaword.interfaces.GigawordDocument;

/**
 *
 */
public class GigawordIngesterTest {

  Path p = Paths.get("src/test/resources/serif_dog-bites-man.sgml");

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  /**
   * @throws IOException
   * @throws ConcreteException
   */
  @Test
  public void pathStringTest() throws IOException, ConcreteException {
    GigawordDocument pdc = new GigawordDocumentConverter().fromPathString(this.p.toString());
    Communication c = new CommunicationizableGigawordDocumentImpl(pdc).toCommunication();
    this.testAgainstDogVsMan(c);
  }

  private void testAgainstDogVsMan(Communication c) throws ConcreteException {
    assertEquals("dog-bites-man_20141009.sgml", c.getId());
    assertEquals("other", c.getType().toLowerCase());

    List<Section> sectionList = c.getSectionList();
    Section title = sectionList.get(0);
    assertEquals("Dog Bites Man\n", new SuperTextSpan(title.getTextSpan(), c).getText());
    assertEquals("Title", title.getKind());

    assertEquals(
        "John Smith, manager of ACME INC, was bit by a dog on March 10th, 2013.\n", new SuperTextSpan(sectionList.get(1).getTextSpan(), c).getText());
    assertEquals("Passage", sectionList.get(1).getKind());

    new SuperCommunication(c).writeToFile(tmpFolder.getRoot().toPath().resolve("test-out.concrete"), true);
  }

  @Test
  public void sgmlStringTest() throws IOException, ConcreteException {
    try (InputStream is = Files.newInputStream(p);
        BufferedInputStream bis = new BufferedInputStream(is)) {
      String sgml = IOUtils.toString(bis, StandardCharsets.UTF_8);
      GigawordDocument pdc = new GigawordDocumentConverter().fromSGMLString(sgml);
      Communication c = new CommunicationizableGigawordDocumentImpl(pdc).toCommunication();
      this.testAgainstDogVsMan(c);
    }
  }
}
