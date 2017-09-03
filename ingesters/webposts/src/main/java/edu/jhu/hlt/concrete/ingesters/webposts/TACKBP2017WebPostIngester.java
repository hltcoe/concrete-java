/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.webposts;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.CloseableThreadContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParametersDelegate;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.IngesterParameterDelegate;
import edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester;
import edu.jhu.hlt.concrete.metadata.tools.SafeTooledAnnotationMetadata;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;
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
public class TACKBP2017WebPostIngester implements SafeTooledAnnotationMetadata, UTF8FileIngester {

  private static final Logger LOGGER = LoggerFactory.getLogger(TACKBP2017WebPostIngester.class);

  public static final String DOC_LOCAL_NAME = "DOC";
  public static final String DATETIME_LOCAL_NAME = "DATE_TIME";
  public static final String HEADLINE_LOCAL_NAME = "HEADLINE";
  public static final String AUTHOR_LOCAL_NAME = "AUTHOR";
  public static final String TEXT_LOCAL_NAME = "TEXT";

  private final XMLInputFactory inF;
  private final DateTimeFormatter dtf;

  /**
   *
   */
  public TACKBP2017WebPostIngester() {
    this.inF = XMLInputFactory.newInstance();
    this.dtf = ISODateTimeFormat.dateTimeParser().withZoneUTC();
    // UTC time!
    // Local time will cause the DST switch exception:
    // 2009-03-08T02:00:02: this instant does not exist under America/NewYork
    // time zone, hence an exception raised
    // To fix this, consider all time as UTC.
    // @tongfei
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return Timing.currentLocalTime();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolName()
   */
  @Override
  public String getToolName() {
    return TACKBP2017WebPostIngester.class.getSimpleName();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.concrete.metadata.tools.MetadataTool#getToolVersion()
   */
  @Override
  public String getToolVersion() {
    return ProjectConstants.VERSION;
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.concrete.ingesters.base.Ingester#getKind()
   */
  @Override
  public String getKind() {
    return "newswire";
  }

  private Section handleHeadline(XMLEventReader rdr, Communication ptr) throws XMLStreamException {
    if (!ptr.isSetText())
      throw new IllegalArgumentException("need comm with text set.");

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
    final String hlText = ptr.getText().substring(charOff, endTextOffset);

    SimpleImmutableEntry<Integer, Integer> pads = Util.trimSpacing(hlText);
    TextSpan ts = new TextSpan(charOff + pads.getKey(), endTextOffset - pads.getValue());

    Section s = new Section();
    s.setKind("headline");
    s.setTextSpan(ts);
    s.addToNumberList(0);
    return s;
  }

  private Section handleBeginning(final XMLEventReader rdr, final String content, final Communication cptr)
      throws XMLStreamException, ConcreteException {
    // "zero" block
    rdr.nextEvent();
    // document block
    XMLEvent docEvent  = rdr.nextEvent();

    // id attr
    Attribute docIDAttr = docEvent.asStartElement().getAttributeByName(QName.valueOf("id"));
    final String docid = docIDAttr.getValue();
    if (!docid.isEmpty())
      cptr.setId(docid);

    // log with the document ID
    try(CloseableThreadContext.Instance ctc = CloseableThreadContext.put("id", "docid");) {
      // whitespace
      rdr.nextEvent();
      // datetime start
      rdr.nextEvent();

      // actual datetime
      XMLEvent dateTimeEvent = rdr.nextEvent();
      String dateTimeString = dateTimeEvent.asCharacters().getData().trim();
      if (!dateTimeString.isEmpty()) {
        // try to parse startTime
        try {
          DateTime parsed = dtf.parseDateTime(dateTimeString);
          cptr.setStartTime(parsed.getMillis() / 1000);
        } catch (IllegalArgumentException e) {
          LOGGER.error("Failed to parse date time: {}", dateTimeString);
        }
      }

      // date time end
      rdr.nextEvent();
      // WS
      rdr.nextEvent();

      // headline start
      final Section s = handleHeadline(rdr, cptr);
      return s;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * edu.jhu.hlt.concrete.ingesters.base.UTF8FileIngester#fromCharacterBasedFile
   * (java.nio.file.Path)
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
        BufferedInputStream bin = new BufferedInputStream(is, 1024 * 8);) {
      content = IOUtils.toString(bin, StandardCharsets.UTF_8);
      c.setText(content);
    } catch (IOException e) {
      throw new IngestException(e);
    }

    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
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

        // headline end, ws, author
        rdr.nextEvent();
        rdr.nextEvent();
        rdr.nextEvent();
        // only run if there's an author
        // author string + section setup
        XMLEvent authorEvent = rdr.nextEvent();
        if (authorEvent.isCharacters()) {
          String author = authorEvent.asCharacters().getData();
          final int authorOff = authorEvent.getLocation().getCharacterOffset();
          TextSpan authorTS = new TextSpan(authorOff, authorOff + author.length());
          Section authorS = new Section();
          authorS.setUuid(g.next());
          authorS.addToNumberList(1);
          authorS.setKind("author");
          authorS.setTextSpan(authorTS);
          c.addToSectionList(authorS);

          // author end
          rdr.nextEvent();
        }
          
        // ws
        rdr.nextEvent();
        // text start
        rdr.nextEvent();
        // actual text
        XMLEvent textEvent = rdr.nextEvent();
        final int textOffset = textEvent.getLocation().getCharacterOffset();
        String text = textEvent.asCharacters().getData();
        // surrounded by newlines so just add 1 to offset

        final int choppedOffset = textOffset + 1;
        final String subtext = c.getText().substring(choppedOffset, choppedOffset + text.length() - 2);

        int currentOffset = choppedOffset;
        int sectNumber = 2;
        int subSect = 0;

        LOGGER.debug("Got text: {}", subtext);
        try (Scanner sc = new Scanner(subtext);) {
          while (sc.hasNextLine()) {
            String line = sc.nextLine();
            LOGGER.debug("Got line: {}", line);
            if (!line.isEmpty()) {
              final int endOffset = currentOffset + line.length();

              // if full of WS - don't add a section here
              if (!line.trim().isEmpty()) {
                Section s = new Section();
                s.setUuid(g.next());
                s.setKind("passage");
                TextSpan ts = new TextSpan(currentOffset, endOffset);
                s.setTextSpan(ts);
                s.addToNumberList(sectNumber);
                s.addToNumberList(subSect);
                subSect++;
                c.addToSectionList(s);
              }

              currentOffset = endOffset;
            }

            // add the line read by the scanner to offset
            currentOffset++;
          }
        }

        return c;
      } catch (XMLStreamException | ConcreteException | StringIndexOutOfBoundsException x) {
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

  private static class Opts {
    @ParametersDelegate
    private IngesterParameterDelegate delegate = new IngesterParameterDelegate();
  }

  public static void main(String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());
    Opts run = new Opts();
    JCommander jc = new JCommander(run, args);
    jc.setProgramName(TACKBP2017WebPostIngester.class.getSimpleName());
    if (run.delegate.help) {
      jc.usage();
      return;
    }

    try {
      Path outpath = Paths.get(run.delegate.outputPath);
      IngesterParameterDelegate.prepare(outpath);
      Path outWithExt = outpath.resolve(run.delegate.filename);

      if (Files.exists(outWithExt)) {
        if (!run.delegate.overwrite) {
          LOGGER.info("File: {} exists and overwrite disabled. Not running.", outWithExt.toString());
          return;
        } else {
          Files.delete(outWithExt);
        }
      }

      TACKBP2017WebPostIngester ing = new TACKBP2017WebPostIngester();
      try (OutputStream os = Files.newOutputStream(outWithExt);
          GzipCompressorOutputStream gout = new GzipCompressorOutputStream(os);
          TarArchiver arch = new TarArchiver(gout)) {
        List<Path> paths = run.delegate.findFilesInPaths();
        LOGGER.info("Preparing to run over {} paths.", paths.size());
        for (Path p : paths) {
          LOGGER.info("Running on file: {}", p.toAbsolutePath().toString());
          new ExistingNonDirectoryFile(p);
          try {
            Communication next = ing.fromCharacterBasedFile(p);
            arch.addEntry(new ArchivableCommunication(next));
          } catch (IngestException e) {
            LOGGER.error("Error processing file: " + p.toString(), e);
          }
        }
      }
    } catch (NotFileException | IOException e) {
      LOGGER.error("Caught exception processing.", e);
    }
  }
}
