/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.bolt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.CommunicationFactory;
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Class representing a Concrete ingester for BOLT forum post data.
 *
 * Currently only extracts the headline and posts from the document.
 */
public class BoltForumPostIngester implements SafeTooledAnnotationMetadata, UTF8FileIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(BoltForumPostIngester.class);

  private final XMLInputFactory inF;

  /**
   *
   */
  public BoltForumPostIngester() {
    this.inF = XMLInputFactory.newInstance();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return Timing.currentLocalTime();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolName()
   */
  @Override
  public String getToolName() {
    return BoltForumPostIngester.class.getSimpleName();
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.Ingester#getKind()
   */
  @Override
  public String getKind() {
    return "forum-post";
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester#fromCharacterBasedFile(java.nio.file.Path)
   */
  @Override
  public Communication fromCharacterBasedFile(final Path path) throws IngestException {
    if (!Files.exists(path))
      throw new IngestException("No file at: " + path.toString());

    Communication c = CommunicationFactory.create();
    c.setType(this.getKind());
    c.setMetadata(TooledMetadataConverter.convert(this));

    try {
      ExistingNonDirectoryFile ef = new ExistingNonDirectoryFile(path);
      c.setId(ef.getName().split("\\.")[0]);
    } catch (NoSuchFileException | NotFileException e) {
      // might throw if path is a directory.
      throw new IngestException(path.toString() + " is not a file, or is a directory.");
    }

    String content;
    try (InputStream is = Files.newInputStream(path);
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 8)) {
      content = IOUtils.toString(bin, StandardCharsets.UTF_8);
      c.setText(content);
    } catch (IOException e) {
      throw new IngestException(e);
    }

    try (InputStream is = Files.newInputStream(path);
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 8)) {
      XMLStreamReader rdr = null;
      try {
        rdr = inF.createXMLStreamReader(bin);
        boolean insideSpan = false;
        boolean insideQuote = false;

        int spanStart = -1;
        while (rdr.hasNext()) {
          final int eventType = rdr.next();
          Location l = rdr.getLocation();
          LOGGER.debug("Offset: {}", l.getCharacterOffset());
          LOGGER.debug("Event type: {}", eventType);
          LOGGER.debug("Prefix: {}", rdr.getPrefix());
          if (eventType == XMLStreamReader.START_ELEMENT) {
            QName qn = rdr.getName();
            final String part = qn.getLocalPart();
            LOGGER.debug("QN: {}", part);

            if (part.equals("quote")) {
              LOGGER.debug("Quote target: {}", rdr.getAttributeValue(0));
              insideQuote = true;
            } else {
              if (part.equals("post")) {
                LOGGER.debug("Author: {}", rdr.getAttributeValue(0));
                LOGGER.debug("Datetime: {}", rdr.getAttributeValue(1));
                LOGGER.debug("id: {}", rdr.getAttributeValue(2));
                // next state will be characters.
                // might be a quote coming - that's handled in the character block below
                insideSpan = true;
                spanStart = l.getCharacterOffset();
              } else if (part.equals("headline")) {
                insideSpan = true;
                spanStart = l.getCharacterOffset();
              }
            }
          }

          if (eventType == XMLStreamReader.END_ELEMENT) {
            QName qn = rdr.getName();
            String part = qn.getLocalPart();
            LOGGER.debug("QN (end): {}", part);
            if (part.equals("quote")) {
              LOGGER.debug("Leaving quote state; updating offset.");
              spanStart = l.getCharacterOffset();
              insideQuote = false;
            }

            // should not hit this - it means that
            // a post was entered but the content was not
            // extracted.
            else if (part.equals("post") && insideSpan) {
              throw new RuntimeException("Entered a 'post' block, but failed to extract any content.");
            }
          }

          if (eventType == XMLStreamReader.CHARACTERS) {
            LOGGER.debug("In characters");
            int len = rdr.getTextLength();
            LOGGER.debug("Len: {}", len);

            // if len == 1, there will be a quote following this.
            // not dealing with quoted text now - don't try to get
            // a textspan - it won't be right anyway.
            if (insideSpan && !insideQuote && len == 1)
              insideQuote = true;
            if (insideSpan && !insideQuote) {
              // sectionable.
              final int lco = l.getCharacterOffset();
              LOGGER.debug("Text span: {} {}", spanStart, lco);
              String subs = content.substring(spanStart, lco);
              LOGGER.debug("Subs: {}", subs);
              // these strings are padded with spaces and newlines
              // for convenience, reduce the textspan so that it
              // starts with a character.
              final int leftPadding = this.getLeftSpacesPaddingCount(subs);
              LOGGER.debug("Left paddding: {}", leftPadding);
              final int rightPadding = this.getRightSpacesPaddingCount(subs);
              LOGGER.debug("Right paddding: {}", rightPadding);

              final String newsubs = content.substring(spanStart + leftPadding, lco - rightPadding);
              LOGGER.debug("New substring: {}", newsubs);
              TextSpan ts = new TextSpan(spanStart + leftPadding, lco - rightPadding);
              try {
                Section s = SectionFactory.fromTextSpan(ts, "post");

                // first section is a headline
                if (!c.isSetSectionList())
                  s.setKind("headline");

                c.addToSectionList(s);
              } catch (ConcreteException e) {
                // won't throw.
              }

              insideSpan = false;
            }
          }
        }

        return c;
      } catch (XMLStreamException x) {
        throw new IngestException(x);
      } finally {
        if (rdr != null)
          try {
            rdr.close();
          } catch (XMLStreamException e) {
            // not likely.
            LOGGER.info("Error closing XMLReader.", e);
          }
      }
    } catch (IOException e) {
      throw new IngestException(e);
    }
  }

  private int getLeftSpacesPaddingCount(final String str) {
    final int len = str.length();
    for (int i = 0; i < len; i++) {
      Character c = str.charAt(i);
      if (!isSpaceOrUnixNewline(c))
        return i;
    }

    return len;
  }

  private boolean isSpaceOrUnixNewline(final Character c) {
    return c.equals(' ') || c.equals('\n');
  }

  private int getRightSpacesPaddingCount(final String str) {
    final int lenIdx = str.length() - 1;
    for (int i = 0; i < lenIdx; i++) {
      Character c = str.charAt(lenIdx - i);
      if (!this.isSpaceOrUnixNewline(c))
        return i;
    }

    return lenIdx + 1;
  }

  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    if (args.length != 2) {
      LOGGER.info("Usage: {} {} {}", BoltForumPostIngester.class.getName(), "/path/to/bolt/.xml/file", "/path/to/output/file");
      System.exit(1);
    }

    Path inputPath = Paths.get(args[0]);
    Path outPath = Paths.get(args[1]);
    Optional.ofNullable(outPath.getParent()).ifPresent(p -> {
      if (!Files.exists(p))
        try {
          Files.createDirectories(p);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
    });

    try {
      BoltForumPostIngester ing = new BoltForumPostIngester();
      Communication c = ing.fromCharacterBasedFile(inputPath);
      new WritableCommunication(c).writeToFile(outPath, true);
    } catch (IngestException | ConcreteException e) {
      LOGGER.error("Caught exception during ingest.", e);
    }
  }
}
