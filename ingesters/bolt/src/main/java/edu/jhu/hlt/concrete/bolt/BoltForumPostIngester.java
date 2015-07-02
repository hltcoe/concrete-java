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
    } else
      throw new IllegalArgumentException("Unhandled tag: " + part);
  }

  private int iterateToCharacters(final XMLEventReader rdr, final int previousOffsetPtr) throws XMLStreamException {
    // Peek at the next element.
    XMLEvent fp = rdr.peek();

    if (fp.isCharacters())
      if (fp.asCharacters().isWhiteSpace()) {
        // whitespace is not desirable. keep moving.
        LOGGER.debug("Got whitespace chars - continuing.");
        rdr.nextEvent();
        return this.iterateToCharacters(rdr, previousOffsetPtr);
      } else {
        LOGGER.debug("Iterator now on non-whitespace chars: {}", fp.asCharacters().getData());
        return previousOffsetPtr;
      }
    else {
      if (fp.isStartElement()) {
        int prevOff = this.handleNonPostStartElement(rdr);
        return this.iterateToCharacters(rdr, prevOff);
      }
      else
        // ???
        throw new IllegalArgumentException("Not sure what to do with end element: " + fp.asEndElement().getName().getLocalPart());
    }
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

  /**
   * @param rdr
   * @param sections
   * @param sectionNumberPtr
   * @param subSectionPtr
   * @param offsetPtr
   *
   * @throws XMLStreamException
   * @throws ConcreteException
   */
  private void handlePosts(final XMLEventReader rdr, final List<Section> sections, final int sectionNumberPtr, final int subSectionPtr, final int offsetPtr, final String contentPtr) throws XMLStreamException, ConcreteException {
    Characters fpChars = rdr.nextEvent().asCharacters();
    int localOffset = offsetPtr;
    if (fpChars.isWhiteSpace()) {
      int newOff = this.iterateToCharacters(rdr, fpChars.getLocation().getCharacterOffset());
      LOGGER.debug("Moving local offset to: {}", newOff);
      localOffset = newOff;
      fpChars = rdr.peek().asCharacters();
    }

    String fpContent = fpChars.getData();
    LOGGER.debug("Character based data: {}", fpContent);

    SimpleImmutableEntry<Integer, Integer> pads = this.trimSpacing(fpContent);
    final int tsb = localOffset + pads.getKey();
    final int tse = localOffset + fpContent.length() - pads.getValue();
    LOGGER.debug("Section text: {}", contentPtr.substring(tsb, tse));
    TextSpan ts = new TextSpan(tsb, tse);
    Section s = SectionFactory.fromTextSpan(ts, "post");
    List<Integer> intList = new ArrayList<>();
    intList.add(sectionNumberPtr);
    intList.add(subSectionPtr);
    sections.add(s);

    int newSubSectionPtr = subSectionPtr + 1;

    XMLEvent next = rdr.peek();
    LOGGER.debug("Peeking at next event.");
    if (next.isEndElement()) {
      LOGGER.debug("Next event is an EndElement.");
      EndElement ee = next.asEndElement();
      String pn = ee.getName().getLocalPart();
      if (pn.equals(POST_LOCAL_NAME)) {
        // Skip the next event, also skip the characters (whitespace) after.
        rdr.nextEvent();
        XMLEvent ce = rdr.nextEvent();
        if (!ce.isCharacters() && !ce.asCharacters().isWhiteSpace())
          throw new IllegalArgumentException("Non-characters or non-whitespace characters follwed the end of a post - unseen case");

        // Reader should now point to a post.
        LOGGER.debug("Finished post.");
        return;
      }
    } else if (next.isCharacters() && next.asCharacters().isWhiteSpace()) {
      // Hyper-peek to see if it's the end.
      next = rdr.nextEvent();
      XMLEvent peek = rdr.peek();
      if (peek.isEndElement()) {
        next = rdr.nextEvent();
        // If end of post, return.
        EndElement ee = next.asEndElement();
        String pn = ee.getName().getLocalPart();
        if (pn.equals(POST_LOCAL_NAME)) {
          // Skip the next event, also skip the characters (whitespace) after.
          XMLEvent ce = rdr.nextEvent();
          if (!ce.isCharacters() && !ce.asCharacters().isWhiteSpace())
            throw new IllegalArgumentException("Non-characters or non-whitespace characters follwed the end of a post - unseen case");

          // Reader should now point to a post.
          LOGGER.debug("Finished post.");
          return;
        }
      } else {
        LOGGER.debug("Peeked and iterated, but wasn't the end.");
        // Move to characters.
        int offset = this.iterateToCharacters(rdr, fpChars.getLocation().getCharacterOffset());
        // Non-post element coming up - recurse.
        LOGGER.debug("Recursing.");
        this.handlePosts(rdr, sections, sectionNumberPtr, newSubSectionPtr, offset, contentPtr);
      }
    } else {
      // Move to characters.
      int offset = this.iterateToCharacters(rdr, fpChars.getLocation().getCharacterOffset());
      // Non-post element coming up - recurse.
      LOGGER.debug("Recursing.");
      this.handlePosts(rdr, sections, sectionNumberPtr, newSubSectionPtr, offset, contentPtr);
    }
  }

  private int handleQuote(final XMLEventReader rdr) throws XMLStreamException {
    // For quotes, there will be character contents - skip for now...
    XMLEvent quoteContent = rdr.nextEvent();
    if (!quoteContent.isCharacters())
      throw new RuntimeException("Characters did not follow quote.");
    // Skip end of quote.
    return rdr.nextEvent().getLocation().getCharacterOffset();
  }

  private int handleImg(final XMLEventReader rdr) throws XMLStreamException {
    // Images should not have anything between start and end.
    // Throw if it does.
    LOGGER.debug("Handling image.");
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
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 8)) {
      content = IOUtils.toString(bin, StandardCharsets.UTF_8);
      c.setText(content);
    } catch (IOException e) {
      throw new IngestException(e);
    }

    try (InputStream is = Files.newInputStream(path);
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8 * 8);) {
      XMLEventReader rdr = null;
      try {
        rdr = inF.createXMLEventReader(bin);

        // Below method moves the reader
        // to the first post element.
        Section headline = this.handleHeadline(rdr, content);
        c.addToSectionList(headline);
        LOGGER.debug("headline text: {}", new SuperTextSpan(headline.getTextSpan(), c).getText());

        // Section indices.
        int sectNumber = 1;
        int subSect = 0;

        // Move iterator to post start element.
        this.iterateToPosts(rdr);

        // First post element.
        while (rdr.hasNext()) {
          XMLEvent nextEvent = rdr.nextEvent();
          // Peek to see if document is going to end.
          if (rdr.peek().isEndDocument())
            break;
          int initOffset = nextEvent.getLocation().getCharacterOffset();
          List<Section> individualPosts = new ArrayList<>();
          this.handlePosts(rdr, individualPosts, sectNumber, subSect, initOffset, content);
          individualPosts.forEach(p -> c.addToSectionList(p));
          sectNumber++;
          subSect = 0;
        }

//        XMLEvent fp = rdr.nextEvent();
//        StartElement fpse = fp.asStartElement();
//        LOGGER.info("First post QN: {}", fpse.getName().getLocalPart());
//        int fpOff = fpse.getLocation().getCharacterOffset();
//        LOGGER.debug("Offset: {}", fpOff);
//        Characters fpChars = rdr.nextEvent().asCharacters();
//        int fpCharOffset = fpChars.getLocation().getCharacterOffset();
//        String fpContent = fpChars.getData();
//        LOGGER.debug("Text of next event: {}", fpContent);
//        LOGGER.debug("Offset of next event: {}", fpCharOffset);
//        LOGGER.debug("Text via offsets: {}", content.substring(fpOff, fpContent.length() + fpOff));
//
//        TextSpan ts = new TextSpan(fpOff, fpContent.length() + fpOff);
//        Section s = SectionFactory.fromTextSpan(ts, "post");
//        List<Integer> intList = new ArrayList<>();
//        intList.add(sectNumber);
//        intList.add(subSect);
//        c.addToSectionList(s);
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
