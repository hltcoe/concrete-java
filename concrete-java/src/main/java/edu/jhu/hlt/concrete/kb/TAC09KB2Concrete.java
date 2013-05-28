/**
 * 
 */
package edu.jhu.hlt.concrete.kb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.Concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Concrete.AttributeMetadata;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.CommunicationGUID;
import edu.jhu.hlt.concrete.Concrete.KnowledgeGraph;
import edu.jhu.hlt.concrete.Concrete.LanguageIdentification;
import edu.jhu.hlt.concrete.Concrete.LanguageIdentification.LanguageProb;
import edu.jhu.hlt.concrete.Concrete.StringAttribute;
import edu.jhu.hlt.concrete.Concrete.UUID;
import edu.jhu.hlt.concrete.Concrete.Vertex;
import edu.jhu.hlt.concrete.Concrete.VertexKindAttribute;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.io.ProtocolBufferWriter;
import edu.jhu.hlt.concrete.util.FileUtil;
import edu.jhu.hlt.concrete.util.IdUtil;

/**
 * @author mayfield
 * @version 1.0
 */

/*

# Required* / Desired Concrete Fields
#   Vertex
#    *uuid
#     kind* (PERSON, ORGANIZATION, GPE, UNKNOWN)
#     name* [StringAttribute: uuid, AttributeMetadata metadata, value]
#   Communication
#    *guid
#    *uuid
#     text
#     (language_id)
#     title
#     kind (WIKIPEDIA)
#    *knowledge_graph 
#   KnowledgeGraph (EMPTY)
#    *uuid
#    *vertex

# Mappings:
#	TACKB			Concrete
#	=====			========
#	wiki_text		Communication->text
#	entity			Vertex
#	entity->wiki_title	Communication->title ??
#	entity->type		Vertex->kind
#	entity->id		hashed or mapped to Vertex->uuid
#	entity->id		  Communication->guid (TAC09KB_E00392)
#	entity->name		Vertex->name (possibly normalized)
#	fact:fullname		Vertex->name
#	fact:???		RawKeyValuePair
#	fact:name		RawKeyValuePair->key
#	fact->{TEXT}		RawKeyValuePair->value

Vertex
  UUID uuid
  VertexKindAttribute kind => {PERSON, ORGANIZATION, GPE, UNKNOWN}
  StringAttribute name*
    UUID uuid
    AttributeMetadata metadata
      string tool
      double timestamp
      float confidence
    string value
  StringAttribute comment*
  CommunicationGUIDAttribute communication_guid*
    UUID uuid
    AttributeMetadata metadata
    CommunicationGUID value

Communication
  CommunicationGUID guid
    string corpus_name      (TAC09KB)
    string communication_id (E000008)
  UUID uuid
  string text
  LanguageIdentification language_id
    UUID uuid
    AnnotationMetadata metadata?
    LanguageProb language*
      string language (ISO 639-3)
      float probability

*/

public class TAC09KB2Concrete {

    private static final Logger logger = LoggerFactory
            .getLogger(TAC09KB2Concrete.class);

    static final AttributeMetadata attribute_metadata;
    static final AnnotationMetadata annotation_metadata;
    static final LanguageProb language_prob;
    static final LanguageIdentification language_id;
    static final double timestamp = (double) ((new Date()).getTime());

    static {
        attribute_metadata = AttributeMetadata.newBuilder().setConfidence(1.0f)
                .setTool("TAC09KB2Concrete.java").setTimestamp(timestamp)
                .build();
        annotation_metadata = AnnotationMetadata.newBuilder()
                .setConfidence(1.0f).setTool("TAC09KB2Concrete.java")
                .setTimestamp(timestamp).build();
        language_prob = LanguageProb.newBuilder().setLanguage("eng")
                .setProbability(1.0f).build();
        language_id = LanguageIdentification.newBuilder()
                .addLanguage(language_prob).setUuid(IdUtil.generateUUID())
                .setMetadata(annotation_metadata).build();
    }

    static final Pattern whitespace_pattern = Pattern.compile("\\s+");

    String current_id = null;
    String current_link = null;
    String fact_name = null;
    CommunicationGUID current_communication_guid = null;
    Vertex.Builder current_unbuilt_vertex = null;
    private Path outputPath;
    private Path commsPath;
    private Path verticesPath;

    public TAC09KB2Concrete(String pathToOutputFiles) throws ConcreteException {
        this.outputPath = Paths.get(pathToOutputFiles);
        this.commsPath = this.outputPath.resolve("comms");
        this.verticesPath = this.outputPath.resolve("vertices");
        
        try {
            if (!Files.exists(this.outputPath))
                Files.createDirectories(this.outputPath);
            if (!Files.exists(this.commsPath))
                FileUtil.deleteFolderAndSubfolders(this.commsPath);
            Files.createDirectories(this.commsPath);
            if (!Files.exists(this.verticesPath))
                FileUtil.deleteFolderAndSubfolders(this.verticesPath);
            Files.createDirectories(this.verticesPath);
        } catch (IOException e) {
            throw new ConcreteException(e);
        }
    }
    
    /**
     * Generate the unique {@link UUID} for a given TAC KBID. (This perhaps
     * belongs in IdUtil.java)
     * 
     * @param tac_id
     *            the TAC09 KBID for the entity (such as E000042)
     * @return a UUID guaranteed to be the same across different calls to this
     *         routine
     */
    public static Concrete.UUID generateUUIDFromTACID(String tac_id) {
        // Tack on a distinguishing salt string to avoid any possible
        // conflicts with other namespaces
        return edu.jhu.hlt.concrete.util.IdUtil.fromJavaUUID(java.util.UUID
                .nameUUIDFromBytes(("Convert TAC ID to UUID: " + tac_id)
                        .getBytes()));
    }

    class KBHandler extends DefaultHandler {

        StringBuilder text_buf = new StringBuilder(10000);
        String text_type = null;

        void collect_text(String type) {
            if (text_type != null)
                logger.error("ERROR: Nested calls to collect_text");
            text_type = type;
            text_buf.setLength(0);
        }

        String retrieve_text(String type) {
            String result = "";
            if (text_type == null)
                logger.error("ERROR: Attempt to collect text for " + type
                        + " that wasn't collected");
            else if (!text_type.equals(type))
                logger.error("ERROR: Text collected for " + text_type
                        + " but retrieved for " + type);
            else
                result = new String(text_buf);
            text_buf.setLength(0);
            text_type = null;
            return (result);
        }

        String normalize(String string) {
            return (whitespace_pattern.matcher(string).replaceAll(" "));
        }

        // void report(String key, String value) {
        // if (current_id == null) {
        // logger.error("Attempt to report with no current ID");
        // }
        // else {
        // logger.info(current_id + "\t" + key + "\t" + normalize(value));
        // }
        // }

        public void startElement(String namespaceURI, String localName,
                String qualifiedName, Attributes attributes)
                throws SAXException {
            if (qualifiedName.equals("entity")) {
                current_id = attributes.getValue("id");
                current_communication_guid = CommunicationGUID.newBuilder()
                        .setCorpusName("TAC09KB")
                        .setCommunicationId(current_id).build();
                current_unbuilt_vertex = Vertex.newBuilder();
                Vertex.Kind v_kind = Vertex.Kind.UNKNOWN;
                String tac_kind = attributes.getValue("type");
                if (tac_kind.equals("PER"))
                    v_kind = Vertex.Kind.PERSON;
                else if (tac_kind.equals("ORG"))
                    v_kind = Vertex.Kind.ORGANIZATION;
                else if (tac_kind.equals("GPE"))
                    v_kind = Vertex.Kind.GPE;
                current_unbuilt_vertex.addKind(VertexKindAttribute.newBuilder()
                        .setValue(v_kind)
                        .setMetadata(attribute_metadata)
                        .setUuid(IdUtil.generateUUID())
                        .build());
                current_unbuilt_vertex.addName(StringAttribute.newBuilder()
                        .setValue(attributes.getValue("name"))
                        .setMetadata(attribute_metadata)
                        .setUuid(IdUtil.generateUUID())
                        .build());
                // report("wiki_title", attributes.getValue("wiki_title"));
            } else if (qualifiedName.equals("fact")) {
                collect_text("fact");
                fact_name = attributes.getValue("name");
            } else if (qualifiedName.equals("link")) {
                current_link = attributes.getValue("entity_id");
            }
        }

        public void endElement(String uri, String localName,
                String qualifiedName) {
            if (qualifiedName.equals("entity")) {
                Vertex vertex = current_unbuilt_vertex
                        .setUuid(IdUtil.generateUUID())
                        .build();
                logger.info("Write vertex for " + current_id);
                try {
                    ProtocolBufferWriter pbw = 
                            new ProtocolBufferWriter(
                                    TAC09KB2Concrete.this.verticesPath
                                        .resolve(IdUtil.uuidToString(vertex.getUuid()) + ".pb"));
                    pbw.write(vertex);
                    pbw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                current_id = null;
                current_communication_guid = null;
            } else if (qualifiedName.equals("fact")) {
                if (fact_name.equals("fullname"))
                    current_unbuilt_vertex.addName(StringAttribute.newBuilder()
                            .setValue(retrieve_text("fact"))
                            .setMetadata(attribute_metadata)
                            .setUuid(IdUtil.generateUUID())
                            .build());
                // report("fact:" + fact_name, retrieve_text("fact"));
                // if (current_link != null)
                // report("link:" + fact_name, current_link);
            } else if (qualifiedName.equals("link")) {
                current_link = null;
            } else if (qualifiedName.equals("wiki_text")) {
                // Contains the body of the Wikipedia article
                String body_text = retrieve_text("wiki_text");
                UUID uuid = IdUtil.generateUUID();
                Communication communication = Communication
                        .newBuilder()
                        .setUuid(uuid)
                        .setGuid(current_communication_guid)
                        .setText(body_text)
                        .addLanguageId(language_id)
                        .setKind(Communication.Kind.WIKIPEDIA)
                        .setKnowledgeGraph(
                                KnowledgeGraph.newBuilder()
                                        .setUuid(IdUtil.generateUUID()).build())
                        .build();
                logger.info("Write conversation for " + current_id);
                try {
                    ProtocolBufferWriter pbw = 
                            new ProtocolBufferWriter(
                                    TAC09KB2Concrete.this.commsPath
                                        .resolve(communication.getGuid().getCommunicationId() + ".pb"));
                    pbw.write(communication);
                    pbw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void endDocument() throws SAXException {
        }

        public void characters(char[] chars, int start, int length) {
            if (text_type != null)
                text_buf.append(chars, start, length);
        }
    }

    

    static public void main(String[] argv) {
        if (argv.length != 2) {
            logger.info("Usage: java TAC09KB2Concrete <tac09-wp-xmlfile> <output-dir>");
            System.exit(1);
        }
        
        try {
            TAC09KB2Concrete transducer = new TAC09KB2Concrete(argv[1]);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            InputStream xmlInput = new FileInputStream(argv[0]);
            SAXParser saxParser = factory.newSAXParser();
            KBHandler kbhandler = transducer.new KBHandler();
            saxParser.parse(xmlInput, kbhandler);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
    }

}
