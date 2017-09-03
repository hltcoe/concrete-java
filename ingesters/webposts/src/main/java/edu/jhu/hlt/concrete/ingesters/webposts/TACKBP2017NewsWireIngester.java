package edu.jhu.hlt.concrete.ingesters.webposts;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.logging.log4j.CloseableThreadContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.util.ProjectConstants;
import edu.jhu.hlt.concrete.util.Timing;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

public class TACKBP2017NewsWireIngester implements SafeTooledAnnotationMetadata, UTF8FileIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(TACKBP2017NewsWireIngester.class);

  private final XMLInputFactory inF;
  private final DateTimeFormatter dtf;

  public TACKBP2017NewsWireIngester() {
    this.inF = XMLInputFactory.newInstance();
    // max: use UTC to be consistent w/ other ingesters.
    this.dtf = new DateTimeFormatterBuilder()
        .appendYear(4, 4)
        .appendMonthOfYear(2)
        .appendDayOfMonth(2)
        .toFormatter()
        .withZoneUTC();
  }

  @Override
  public long getTimestamp() {
    return Timing.currentLocalTime();
  }

  @Override
  public String getToolName() {
    return TACKBP2017NewsWireIngester.class.getSimpleName();
  }

  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  @Override
  public String getKind() {
    return "newswire";
  }

  final long inferStartTimeFromID(String id) throws IngestException {
    // the ID format is sth like
    // NYT_ENG_20131231.0085
    String[] byUnderscore = id.split("_");
    // take 3rd element of _ split
    String[] byDot = byUnderscore[2].split("\\.");
    // take 1st element of . split
    try {
      DateTime dt = this.dtf.parseDateTime(byDot[0]);
      return dt.getMillis() / 1000;
    } catch (IllegalArgumentException e) {
      throw new IngestException("Failed to parse datetime from ID", e);
    }
  }

  private Section handleBeginning(final XMLEventReader rdr, final Communication c) throws XMLStreamException, IngestException {
    if (!c.isSetText())
      throw new IllegalArgumentException("set communication.text before calling this method");
    // get to the DOC start element
    Util.handleDocumentStartWithDocIDBlock(rdr, c);
    // these don't have datetime blocks - try to infer the start time from the ID
    c.setStartTime(this.inferStartTimeFromID(c.getId()));

    // log with the document ID
    try(CloseableThreadContext.Instance ctc = CloseableThreadContext.put("id", c.getId());) {
      // whitespace
      rdr.nextEvent();

      // headline
      return Util.handleHeadline(rdr, c);
    }
  }

  @Override
  public Communication fromCharacterBasedFile(Path path) throws IngestException {
    if (!Files.exists(path))
      throw new IngestException("No file at: " + path.toString());

    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator g = f.create();
    Communication c = new Communication();
    c.setUuid(g.next());
    c.setType(this.getKind());
    c.setMetadata(TooledMetadataConverter.convert(this));

    try {
      Util.setCommunicationTextToPathContents(path, c);
    } catch (IOException e) {
      throw new IngestException(e);
    }

    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
      XMLEventReader rdr = null;
      try {
        rdr = inF.createXMLEventReader(reader);
        Section s = handleBeginning(rdr, c);
        s.setUuid(g.next());
        c.addToSectionList(s);
        // headline end, ws, text start, ws
        for (int i = 0; i < 4; i++)
          rdr.nextEvent();

        int sectionNumber = 1;
        int subSection = 0;

        // pointing to p start
        // Ps come in pairs. loop until no P start is found.
        XMLEvent next = rdr.nextEvent();
        while (next.isStartElement()) {
          // next is currently a P start.
          // advance + read
          XMLEvent paraEvent = rdr.nextEvent();
          LOGGER.debug("Got para event: {}", paraEvent.getEventType());
          Characters paraText = paraEvent.asCharacters();
          final int paraTrueOffset = paraText.getLocation().getCharacterOffset();
          // move forward 1 to avoid newline
          final int paraOffsetWithoutNewline = paraTrueOffset + 1;
          final String content = paraText.getData();
          LOGGER.debug("Got para content: {}", content);

          // make a section out of this P block
          final Section toAdd = new Section();
          toAdd.setUuid(g.next());
          toAdd.setKind("passage");
          toAdd.addToNumberList(sectionNumber);
          toAdd.addToNumberList(subSection);
          subSection++;

          final TextSpan sts = new TextSpan(paraOffsetWithoutNewline, paraOffsetWithoutNewline + content.length() - 2);
          toAdd.setTextSpan(sts);

          c.addToSectionList(toAdd);
          // next element is P end and a ws element
          rdr.nextEvent();
          rdr.nextEvent();
          // next is another P start (or not and the loop breaks)
          next = rdr.nextEvent();
        }
        return c;
      } catch (XMLStreamException |  StringIndexOutOfBoundsException x) {
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
}
