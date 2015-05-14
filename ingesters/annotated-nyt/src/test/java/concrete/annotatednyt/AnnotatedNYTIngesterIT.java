/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package concrete.annotatednyt;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nytlabs.corpus.NYTCorpusDocument;
import com.nytlabs.corpus.NYTCorpusDocumentParser;

import concrete.validation.CommunicationValidator;
import edu.jhu.hlt.acute.iterators.tar.TarGzArchiveEntryByteIterator;
import edu.jhu.hlt.annotatednyt.AnnotatedNYTDocument;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ingesters.annotatednyt.CommunicationizableAnnotatedNYTDocument;

public class AnnotatedNYTIngesterIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedNYTIngesterIT.class);

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
    Map<String, Path> failureMap = new HashMap<>();
    try (Stream<Path> nytTgzPaths = Files.list(dataPath);) {
      nytTgzPaths
        .flatMap(subfolder -> {
          try {
            return Files.list(subfolder);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
      .filter(tgz -> tgz.toString().endsWith(".tgz"))
      .forEach(p -> {
        LOGGER.info("On path: {}", p.toString());
        try(InputStream is = Files.newInputStream(p);
            BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 24);
            TarGzArchiveEntryByteIterator iter = new TarGzArchiveEntryByteIterator(bin);) {
          while (iter.hasNext()) {
            byte[] n = iter.next();
            NYTCorpusDocument doc = this.parser.fromByteArray(n, false);
            AnnotatedNYTDocument adoc = new AnnotatedNYTDocument(doc);
            Communication c = new CommunicationizableAnnotatedNYTDocument(adoc).toCommunication();
            final String cid = c.getId();
            LOGGER.debug("Successfully got communication: {}", cid);
            boolean isValid = new CommunicationValidator(c).validate();
            if (!isValid)
              failureMap.put(cid, p);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
      
      if (failureMap.size() > 0) {
        LOGGER.warn("There are failures.");
        for (Entry<String, Path> e : failureMap.entrySet()) {
          final Path p = e.getValue();
          final int nPaths = p.getNameCount();
          final String part = p.getName(nPaths - 1).toString();
          LOGGER.warn("ID {} is invalid. File: {}", e.getKey(), part);
        }
        
        fail();
      }
    }
  }
}
