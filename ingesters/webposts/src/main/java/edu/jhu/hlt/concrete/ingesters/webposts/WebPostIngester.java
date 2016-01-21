/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.webposts;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import edu.jhu.hlt.utilt.io.ExistingNonDirectoryFile;
import edu.jhu.hlt.utilt.io.NotFileException;

/**
 * Class representing a Concrete ingester for web post data.
 */
public class WebPostIngester implements SafeTooledAnnotationMetadata, UTF8FileIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebPostIngester.class);

  public static final String DOC_LOCAL_NAME = "DOC";
  public static final String DOCID_LOCAL_NAME = "DOCID";
  public static final String DOCTYPE_LOCAL_NAME = "DOCTYPE";
  public static final String DATETIME_LOCAL_NAME = "DATETIME";
  public static final String BODY_LOCAL_NAME = "BODY";
  public static final String HEADLINE_LOCAL_NAME = "HEADLINE";
  public static final String TEXT_LOCAL_NAME = "TEXT";
  public static final String POST_LOCAL_NAME = "POST";
  public static final String POSTER_LOCAL_NAME = "POSTER";
  public static final String POSTDATE_LOCAL_NAME = "POSTDATE";

  private final XMLInputFactory inF;
  private final DateTimeFormatter dtf;


  /**
   *
   */
  public WebPostIngester() {
    this.inF = XMLInputFactory.newInstance();
    this.dtf = ISODateTimeFormat.dateTimeParser();
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
    return WebPostIngester.class.getSimpleName();
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
    return "web";
  }

  private Section handleBeginning(final XMLEventReader rdr, final String content, final Communication cptr) throws XMLStreamException, ConcreteException {
    // The first type is always a document start event. Skip it.
    rdr.nextEvent();

    // The second type is a document block. Skip it.
    rdr.nextEvent();

    // The third type is a whitespace block. Skip it.
    rdr.nextEvent();

    // The next type is a docid start tag.
    rdr.nextEvent();

    // Text of the docid.
    Characters cc = rdr.nextEvent().asCharacters();
    String idTxt = cc.getData().trim();
    cptr.setId(idTxt);

    // The next part is the docid end element. Skip.
    rdr.nextEvent();

    // Whitespace. Skip.
    rdr.nextEvent();

    // Reader is now pointing at the doctype.
    // XMLEvent doctypeStart = rdr.nextEvent();
    rdr.nextEvent();
    // StartElement dtse = doctypeStart.asStartElement();

    // Doc type content.
    Characters docTypeChars = rdr.nextEvent().asCharacters();
    String docTypeContent = docTypeChars.getData().trim();
    cptr.setType(docTypeContent);

    // Doctype end. Skip.
    rdr.nextEvent();
    // Whitespace. skip.
    rdr.nextEvent();
    // Datetime start.
    rdr.nextEvent();

    // Datetime value.
    Characters dtChars = rdr.nextEvent().asCharacters();
    // TODO: parse this

    String dtValue = dtChars.getData().trim();
    DateTime dt = this.dtf.parseDateTime(dtValue).toDateTime(DateTimeZone.UTC);
    LOGGER.debug("Got DateTime: {}", dt.toString());
    long millis = dt.getMillis();
    cptr.setStartTime(millis / 1000);

    // Datetime end.
    rdr.nextEvent();
    // WS
    rdr.nextEvent();
    // Body begin.
    rdr.nextEvent();
    // WS
    rdr.nextEvent();

    // Headline begin.
    XMLEvent hl = rdr.nextEvent();
    StartElement hlse = hl.asStartElement();
    QName hlqn = hlse.getName();
    final String hlPart = hlqn.getLocalPart();
    LOGGER.debug("QN: {}", hlPart);

    // Headline text.
    Characters hlChars = rdr.nextEvent().asCharacters();
    final int charOff = hlChars.getLocation().getCharacterOffset();
    final int clen = hlChars.getData().length();

    // Construct section, text span, etc.
    final int endTextOffset = charOff + clen;
    final String hlText = content.substring(charOff, endTextOffset);

    SimpleImmutableEntry<Integer, Integer> pads = trimSpacing(hlText);
    TextSpan ts = new TextSpan(charOff + pads.getKey(), endTextOffset - pads.getValue());

    Section s = new Section();
    s.setKind("headline");
    s.setTextSpan(ts);
    List<Integer> intList = new ArrayList<>();
    intList.add(0);
    s.setNumberList(intList);
    return s;
  }

  private static SimpleImmutableEntry<Integer, Integer> trimSpacing(final String str) {
    final int leftPadding = getLeftSpacesPaddingCount(str);
    LOGGER.trace("Left padding: {}", leftPadding);
    final int rightPadding = getRightSpacesPaddingCount(str);
    LOGGER.trace("Right padding: {}", rightPadding);
    return new SimpleImmutableEntry<Integer, Integer>(leftPadding, rightPadding);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester#fromCharacterBasedFile(java.nio.file.Path)
   */
  @Override
  public Communication fromCharacterBasedFile(final Path path) throws IngestException {
    if (!Files.exists(path))
      throw new IngestException("No file at: " + path.toString());

    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator g = f.create();
    Communication c = new Communication();
    c.setUuid(g.next());
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
        // to the headline end element.
        Section headline = this.handleBeginning(rdr, content, c);
        headline.setUuid(g.next());
        c.addToSectionList(headline);
        TextSpan sts = headline.getTextSpan();
        LOGGER.debug("headline text: {}", c.getText().substring(sts.getStart(), sts.getEnding()));

        int sectNumber = 1;
        int subSect = 0;

        int currOff = -1;
        // Big amounts of characters.
        while (rdr.hasNext()) {
          XMLEvent nextEvent = rdr.nextEvent();
          currOff = nextEvent.getLocation().getCharacterOffset();

          // First: see if document is going to end.
          // If yes: exit.
          if (nextEvent.isEndDocument())
            break;

          // Check if start element.
          if (nextEvent.isCharacters()) {
            Characters chars = nextEvent.asCharacters();
            if (!chars.isWhiteSpace()) {
              String fpContent = chars.getData();
              LOGGER.debug("Character offset: {}", currOff);
              LOGGER.debug("Character based data: {}", fpContent);

              SimpleImmutableEntry<Integer, Integer> pads = trimSpacing(fpContent);
              final int tsb = currOff + pads.getKey();
              final int tse = currOff + fpContent.length() - (pads.getValue());
              LOGGER.debug("Section text: {}", content.substring(tsb, tse));
              TextSpan ts = new TextSpan(tsb, tse);
              String sk;
              if (subSect == 0)
                sk = "poster";
              else if (subSect == 1)
                sk = "postdate";
              else
                sk = "post";

              Section s = new Section();
              s.setKind(sk);
              s.setTextSpan(ts);
              s.setUuid(g.next());
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
            LOGGER.debug("Hit end element: {}", localName);
            if (localName.equalsIgnoreCase(POST_LOCAL_NAME)) {
              LOGGER.debug("Switching to new post.");
              sectNumber++;
              subSect = 0;
            } else if (localName.equalsIgnoreCase(TEXT_LOCAL_NAME)) {
              // done with document.
              break;
            }
          }
        }

        return c;

      } catch (XMLStreamException | ConcreteException | StringIndexOutOfBoundsException | ClassCastException x) {
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

  private static int getLeftSpacesPaddingCount(final String str) {
    final int len = str.length();
    for (int i = 0; i < len; i++) {
      Character c = str.charAt(i);
      if (!isSpaceOrUnixNewline(c))
        return i;
    }

    return len;
  }

  private static boolean isSpaceOrUnixNewline(final Character c) {
    return c.equals(' ') || c.equals('\n');
  }

  private static int getRightSpacesPaddingCount(final String str) {
    final int lenIdx = str.length() - 1;
    for (int i = 0; i < lenIdx; i++) {
      Character c = str.charAt(lenIdx - i);
      if (!isSpaceOrUnixNewline(c))
        return i;
    }

    return lenIdx + 1;
  }

  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    if (args.length < 2) {
      LOGGER.info("Usage: {} {} {} {}", WebPostIngester.class.getName(), "/path/to/output/folder", "/path/to/web/.xml/file", "<additional/xml/file/paths>");
      System.exit(1);
    }

    Path outPath = Paths.get(args[0]);
    Optional.ofNullable(outPath.getParent()).ifPresent(p -> {
      if (!Files.exists(p))
        try {
          Files.createDirectories(p);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
    });

    if (!Files.isDirectory(outPath)) {
      LOGGER.error("Output path must be a directory.");
      System.exit(1);
    }

    WebPostIngester ing = new WebPostIngester();
    for (int i = 1; i < args.length; i++) {
      Path lp = Paths.get(args[i]);
      LOGGER.info("On path: {}", lp.toString());
      try {
        Communication c = ing.fromCharacterBasedFile(lp);
        new WritableCommunication(c).writeToFile(outPath.resolve(c.getId() + ".comm"), true);
      } catch (IngestException | ConcreteException e) {
        LOGGER.error("Caught exception during ingest on file: " + args[i], e);
      }
    }
  }
}
