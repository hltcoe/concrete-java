/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.bolt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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
import edu.jhu.hlt.concrete.util.SuperTextSpan;
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

  public static final String POST_LOCAL_NAME = "post";
  public static final String IMG_LOCAL_NAME = "img";
  public static final String QUOTE_LOCAL_NAME = "quote";
  public static final String LINK_LOCAL_NAME = "a";

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

  private Section handleHeadline(final XMLEventReader rdr, final String content) throws XMLStreamException, ConcreteException {
    // The first type is always a document start event. Skip it.
    rdr.nextEvent();

    // The second type is a document ID block. Skip it.
    rdr.nextEvent();

    // The third type is a whitespace block. Skip it.
    rdr.nextEvent();

    // The next type is a headline start tag.
    XMLEvent hl = rdr.nextEvent();
    StartElement hlse = hl.asStartElement();
    QName hlqn = hlse.getName();
    final String hlPart = hlqn.getLocalPart();
    LOGGER.debug("QN: {}", hlPart);
    int hlPartOff = hlse.getLocation().getCharacterOffset();
    LOGGER.debug("HL part offset: {}", hlPartOff);

    // Text of the headline. This would be useful for purely getting
    // the content, but for offsets, it's not that useful.
    Characters cc = rdr.nextEvent().asCharacters();
    int clen = cc.getData().length();

    // The next part is the headline end element. Skip.
    rdr.nextEvent();

    // Whitespace. Skip.
    rdr.nextEvent();

    // Reader is now pointing at the first post.
    // Construct section, text span, etc.
    final int endHlOrigText = hlPartOff + clen;
    final String hlText = content.substring(hlPartOff, endHlOrigText);

    SimpleImmutableEntry<Integer, Integer> pads = this.trimSpacing(hlText);
    TextSpan ts = new TextSpan(hlPartOff + pads.getKey(), endHlOrigText - pads.getValue());

    Section s = SectionFactory.fromTextSpan(ts, "headline");
    List<Integer> intList = new ArrayList<>();
    intList.add(0);
    s.setNumberList(intList);
    return s;
  }

  private SimpleImmutableEntry<Integer, Integer> trimSpacing(final String str) {
    final int leftPadding = this.getLeftSpacesPaddingCount(str);
    LOGGER.trace("Left padding: {}", leftPadding);
    final int rightPadding = this.getRightSpacesPaddingCount(str);
    LOGGER.trace("Right padding: {}", rightPadding);
    return new SimpleImmutableEntry<Integer, Integer>(leftPadding, rightPadding);
  }

  private int handleLink(final XMLEventReader rdr) throws XMLStreamException {
    // Links have a start element, characters, and end element.
    // Method is v. similar to handleQuotes, but may want to actually capture
    // these characters at some point.
    XMLEvent linkContent = rdr.nextEvent();
    if (!linkContent.isCharacters())
      throw new RuntimeException("Characters did not follow link.");
    // Skip end of link.
    return rdr.nextEvent().getLocation().getCharacterOffset();
  }

  /**
   * Moves the rdr "iterator" past any img tags or quote tags.
   *
   * @param rdr
   * @throws XMLStreamException
   */
  private int handleNonPostStartElement(final XMLEventReader rdr) throws XMLStreamException {
    // Next is a start element. Throw if not.
    StartElement se = rdr.nextEvent().asStartElement();
    QName seqn = se.getName();
    String part = seqn.getLocalPart();

    if (part.equals(QUOTE_LOCAL_NAME)) {
      return this.handleQuote(rdr);
    } else if (part.equals(IMG_LOCAL_NAME)) {
      return this.handleImg(rdr);
    } else if (part.equals(LINK_LOCAL_NAME)) {
      return this.handleLink(rdr);
    } else
      throw new IllegalArgumentException("Unhandled tag: " + part);
  }

  /**
   * Move the iterator so that a call to nextEvent will return the beginning of a post tag.
   *
   * @param rdr
   * @throws XMLStreamException
   */
  private void iterateToPosts(final XMLEventReader rdr) throws XMLStreamException {
    // Peek at the next element.
    XMLEvent fp = rdr.peek();

    // If start element and part == "post", return.
    if (fp.isStartElement()) {
      StartElement se = fp.asStartElement();
      if (se.getName().getLocalPart().equals(POST_LOCAL_NAME))
        return;
      else
        // Churn through non-post start tags.
        this.handleNonPostStartElement(rdr);
    } else
      // Drop.
      rdr.nextEvent();

    this.iterateToPosts(rdr);
  }

  private int handleQuote(final XMLEventReader rdr) throws XMLStreamException {
    // For quotes, there will be character contents - skip for now...
    XMLEvent quoteContent = rdr.nextEvent();
    if (!quoteContent.isCharacters())
      throw new RuntimeException("Characters did not follow quote.");
    // Skip end of quote.
    XMLEvent next = rdr.nextEvent();
    // Exit loop only when next end quote is hit.
    boolean hitEndQuoteElement = false;
    while (!next.isEndElement() && !hitEndQuoteElement) {
      // Move to next element.
      next = rdr.nextEvent();
      // If next element is an end element,
      // see if it's an end quote.
      // If so, exit the loop.
      if (next.isEndElement())
        hitEndQuoteElement = next.asEndElement().getName().getLocalPart().equals("quote");
    }

    return next.getLocation().getCharacterOffset();
  }

  private int handleImg(final XMLEventReader rdr) throws XMLStreamException {
    // Images should not have anything between start and end.
    // Throw if it does.
    return rdr.nextEvent().asEndElement().getLocation().getCharacterOffset();
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
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 8);) {
      content = IOUtils.toString(bin, StandardCharsets.UTF_8);
      c.setText(content);
    } catch (IOException e) {
      throw new IngestException(e);
    }

    try (InputStream is = Files.newInputStream(path);
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 8);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bin, StandardCharsets.UTF_8));) {
      XMLEventReader rdr = null;
      try {
        rdr = inF.createXMLEventReader(reader);

        // Below method moves the reader
        // to the first post element.
        Section headline = this.handleHeadline(rdr, content);
        c.addToSectionList(headline);
        LOGGER.info("headline text: {}", new SuperTextSpan(headline.getTextSpan(), c).getText());

        // Section indices.
        int sectNumber = 1;
        int subSect = 0;

        // Move iterator to post start element.
        this.iterateToPosts(rdr);

        // Offset pointer.
        int currOff = -1;

        // First post element.
        while (rdr.hasNext()) {
          XMLEvent nextEvent = rdr.nextEvent();
          // currOff = nextEvent.getLocation().getCharacterOffset();
          // First: see if document is going to end.
          // If yes: exit.
          if (nextEvent.isEndDocument())
            break;

          // XMLEvent peeker = rdr.peek();

          // Check if start element.
          if (nextEvent.isStartElement()) {
            StartElement se = nextEvent.asStartElement();
            QName name = se.getName();
            final String localName = name.getLocalPart();

            // Move past quotes, images, and links.
            if (localName.equals(QUOTE_LOCAL_NAME)) {
              currOff = this.handleQuote(rdr);
              continue;
            } else if (localName.equals(IMG_LOCAL_NAME)) {
              currOff = this.handleImg(rdr);
              continue;
            } else if (localName.equals(LINK_LOCAL_NAME)) {
              currOff = this.handleLink(rdr);
              continue;
            } else if (localName.equals(POST_LOCAL_NAME)) {
              // Start of a new post?
              currOff = se.getLocation().getCharacterOffset();
            } else {
              // unseen edge case.
              throw new IllegalArgumentException("Unsure what to do about start tag: " + localName);
            }
          } else if (nextEvent.isCharacters()) {
            Characters chars = nextEvent.asCharacters();
            if (chars.isWhiteSpace()) {
              currOff = chars.getLocation().getCharacterOffset();
            } else {
              // content to be captured
              String fpContent = chars.getData();
              LOGGER.debug("Character based data: {}", fpContent);

              SimpleImmutableEntry<Integer, Integer> pads = this.trimSpacing(fpContent);
              final int tsb = currOff + pads.getKey();
              final int tse = currOff + fpContent.length() - pads.getValue();
              LOGGER.info("Section text: {}", content.substring(tsb, tse));
              TextSpan ts = new TextSpan(tsb, tse);
              Section s = SectionFactory.fromTextSpan(ts, "post");
              List<Integer> intList = new ArrayList<>();
              intList.add(sectNumber);
              intList.add(subSect);
              s.setNumberList(intList);
              c.addToSectionList(s);

              subSect++;
            }
          } else if (nextEvent.isEndElement()) {
            EndElement ee = nextEvent.asEndElement();
            currOff = ee.getLocation().getCharacterOffset();
            QName name = ee.getName();
            String localName = name.getLocalPart();
            if (localName.equalsIgnoreCase(POST_LOCAL_NAME)) {
              sectNumber++;
              subSect = 0;
            }
          }
        }
        return c;
      } catch (XMLStreamException | ConcreteException x) {
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
