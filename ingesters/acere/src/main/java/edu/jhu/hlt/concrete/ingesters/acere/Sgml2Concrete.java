package edu.jhu.hlt.concrete.ingesters.acere;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.WritableCommunication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * Converts an SGML file to a Concrete Communication. The SGML tags and comments
 * are NOT included in the original text content. They are instead assumed to
 * mark section boundaries.
 *
 * Sections which would correspond to whitespace only are prepended to the
 * following section.
 *
 * This code was designed for and tested on the .sgm files from ACE 2005.
 */
public class Sgml2Concrete {

  Pattern SGML_RE = Pattern.compile("<[^<>]+>");
  private static final String SGML_SECTION_KIND = "SGML_section";
  private static final Pattern whitespace = Pattern.compile("^\\s*$");
  private static final Logger log = LoggerFactory.getLogger(Sgml2Concrete.class);

  private static final String toolname = "ACE 2005 SGML to Concrete converter";

  /**
   * Reads an SGML file and write out a Concrete communication file.
   */
  public void sgmlFile2CommFile(Path xmlFile, Path commFile) throws XMLStreamException, ConcreteException, IOException {
    Communication comm = sgmlFile2Comm(xmlFile);
    new WritableCommunication(comm).writeToFile(commFile, true);
  }

  /**
   * Reads an SGML file and gets a Concrete communication. This method treats
   * the input as a raw sequence of UTF-8 characters, and therefore handles
   * malformed XML. The ACE .sgm files fall into this category since they
   * contain unescaped ampersands. The ACE .apf.xml files also assume
   * that the properly escaped ampersands (i.e. '&amp;') will be left unescaped.
   */
  public Communication sgmlFile2Comm(Path xmlFile) throws FactoryConfigurationError, XMLStreamException, IOException {
    // Read SGML file.
    log.info("Reading SGML file: " + xmlFile);
    byte[] bytez = Files.readAllBytes(xmlFile);
    String sgml = new String(bytez, "UTF-8");

    // Get the raw text and section boundaries.
    List<Integer> sectionMarkers = new ArrayList<Integer>();
    StringBuffer rawTextSb = new StringBuffer();
    Matcher matcher = SGML_RE.matcher(sgml);
    int start = 0;
    while (matcher.find()) {
      int end = matcher.start();
      // Mark a section boundary.
      sectionMarkers.add(rawTextSb.length());
      // Append characters to the buffer.
      rawTextSb.append(sgml.substring(start, end));
      start = matcher.end();
    }
    String rawText = rawTextSb.toString();

    // Create the Sections in a Communication.
    log.info("Creating Communication");
    String id = xmlFile.getFileName().toString().replace(".sgm", "");
    String type = "ACE";
    Communication comm = getComm(rawText, sectionMarkers, id, type);
    return comm;
  }

  /**
   * Reads an XML file and gets a Concrete communication. This differs from the
   * above sgmlFile2Comm, in that it treats the input as proper XML, which most
   * ACE .sgm files are not.
   */
  public Communication xmlFile2Comm(Path xmlFile)
      throws FactoryConfigurationError, XMLStreamException, FileNotFoundException {
    // Read SGML file.
    log.info("Reading SGML file: " + xmlFile);
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLEventReader r = factory.createXMLEventReader(new FileInputStream(xmlFile.toFile()), "UTF-8");

    // Get the raw text and section boundaries.
    List<Integer> sectionMarkers = new ArrayList<Integer>();
    StringBuffer rawTextSb = new StringBuffer();
    while (r.hasNext()) {
      XMLEvent e = r.nextEvent();
      log.trace("Event: " + e.toString());

      switch (e.getEventType()) {
      case XMLEvent.START_ELEMENT:
      case XMLEvent.END_ELEMENT:
      case XMLEvent.COMMENT:
        // Mark a section boundary.
        sectionMarkers.add(rawTextSb.length());
        break;
      case XMLEvent.CHARACTERS:
      case XMLEvent.CDATA:
      case XMLEvent.SPACE:
        // Append characters to the buffer.
        rawTextSb.append(e.asCharacters().getData());
      case XMLEvent.START_DOCUMENT:
      case XMLEvent.END_DOCUMENT:
      case XMLEvent.ATTRIBUTE:
      case XMLEvent.DTD:
      case XMLEvent.ENTITY_REFERENCE:
        // Do nothing.
        break;
      case XMLEvent.PROCESSING_INSTRUCTION:
        throw new IllegalStateException("Unhandled event: " + e);
      default:
        throw new IllegalStateException("Unhandled event: " + e);
      }
    }
    String rawText = rawTextSb.toString();

    // Create the Sections in a Communication.
    log.info("Creating Communication");
    String id = xmlFile.getFileName().toString().replace(".xml", "");
    String type = "XML";
    Communication comm = getComm(rawText, sectionMarkers, id, type);
    return comm;
  }

  /**
   * Create a communication from raw text, using the given section markers to
   * add section segmentations.
   *
   * @param rawText
   *          The raw text.
   * @param sectionMarkers
   *          The section markers.
   * @param id
   *          The ID for the Communication.
   * @param type
   *          The type for the Communication.
   * @return The communication with a single section segmentation containing a
   *         list of sections.
   */
  public Communication getComm(String rawText, List<Integer> sectionMarkers, String id, String type) {
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator g = f.create();
    Communication comm = new Communication();
    comm.setUuid(g.next());
    comm.setId(id);
    comm.setType(type);
    comm.setMetadata(ConcreteUtils.metadata(toolname));

    // Set the source text fields.
    comm.setText(rawText);
    comm.setOriginalText(rawText);

    // Create the sections.
    int start = 0;
    for (int end : sectionMarkers) {
      if (start == end) {
        // Skip empty sections.
        continue;
      }
      String substr = rawText.substring(start, end);
      if (whitespace.matcher(substr).find()) {
        // Don't break the section if it only consists of whitespace.
        continue;
      }
      Section section = new Section(g.next(), SGML_SECTION_KIND);
      TextSpan span = new TextSpan(start, end);
      section.setTextSpan(span);
      log.trace("Span: " + substr);
      comm.addToSectionList(section);
      start = end;
    }

    return comm;
  }

  /**
   * Example usage:
   * <br>
   * <br>
   * <pre>
   * java edu.jhu.hlt.concrete.ingesters.acere.Sgml2Concrete data/ace2005_small/nw/AFP_ENG_20030304.0250.sgm out.comm
   * </pre>
   */
  public static void main(String[] args) throws Exception {
    Path aceSgmFile = Paths.get(args[0]);
    Path commFile = Paths.get(args[1]);
    Sgml2Concrete a2c = new Sgml2Concrete();
    a2c.sgmlFile2CommFile(aceSgmFile, commFile);
  }

}
