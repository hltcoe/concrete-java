/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package concrete.annotatednyt;

import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nytlabs.corpus.NYTCorpusDocument;
import com.nytlabs.corpus.NYTCorpusDocumentParser;

import concrete.validation.CommunicationValidator;
import edu.jhu.hlt.acute.iterators.tar.TarGzArchiveEntryByteIterator;
import edu.jhu.hlt.annotatednyt.AnnotatedNYTDocument;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.annotatednyt.CommunicationizableAnnotatedNYTDocument;

public class AnnotatedNYTIngesterIT {
  final Path dataPath = Paths.get(System.getProperty("anytDataPath"));
  final NYTCorpusDocumentParser parser = new NYTCorpusDocumentParser();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    
  }
  
  @Test
  public void runAcrossAllArchives() throws Exception {
    try (Stream<Path> nytTgzPaths = Files.list(dataPath);) {
      nytTgzPaths
        .flatMap(subfolder -> {
          try {
            return Files.list(subfolder); 
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      nytTgzPaths
        .filter(tgz -> tgz.toString().endsWith(".tgz"));
      nytTgzPaths.forEach(p -> {
        try(InputStream is = Files.newInputStream(p);
            BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 24);
            TarGzArchiveEntryByteIterator iter = new TarGzArchiveEntryByteIterator(bin);) {
          while (iter.hasNext()) {
            byte[] n = iter.next();
            NYTCorpusDocument doc = this.parser.fromByteArray(n, false);
            AnnotatedNYTDocument adoc = new AnnotatedNYTDocument(doc);
            Communication c = new CommunicationizableAnnotatedNYTDocument(adoc).toCommunication();
            boolean isValid = new CommunicationValidator(c).validate();
            assertTrue("Communication " + c.getId() + " is invalid!", isValid);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }
}
