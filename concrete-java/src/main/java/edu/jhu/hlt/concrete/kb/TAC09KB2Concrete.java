/**
 * 
 */
package edu.jhu.hlt.concrete.kb;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map.Entry;
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
import edu.jhu.hlt.concrete.Concrete.CommunicationGUIDAttribute;
import edu.jhu.hlt.concrete.Concrete.KnowledgeGraph;
import edu.jhu.hlt.concrete.Concrete.LanguageIdentification;
import edu.jhu.hlt.concrete.Concrete.LanguageIdentification.LanguageProb;
import edu.jhu.hlt.concrete.Concrete.StringAttribute;
import edu.jhu.hlt.concrete.Concrete.UUID;
import edu.jhu.hlt.concrete.Concrete.Vertex;
import edu.jhu.hlt.concrete.Concrete.VertexKindAttribute;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.util.FileUtil;
import edu.jhu.hlt.concrete.util.IdUtil;
import edu.jhu.hlt.concrete.util.ProtoFactory;

/**
 * @author mayfield
 * @version 1.0
 */

/*
 * 
 * # Required* / Desired Concrete Fields # Vertex # *uuid # kind* (PERSON,
 * ORGANIZATION, GPE, UNKNOWN) # name* [StringAttribute: uuid, AttributeMetadata
 * metadata, value] # Communication # *guid # *uuid # text # (language_id) #
 * title # kind (WIKIPEDIA) # *knowledge_graph # KnowledgeGraph (EMPTY) # *uuid
 * # *vertex
 * 
 * # Mappings: # TACKB Concrete # ===== ======== # wiki_text Communication->text
 * # entity Vertex # entity->wiki_title Communication->title ?? # entity->type
 * Vertex->kind # entity->id hashed or mapped to Vertex->uuid # entity->id
 * Communication->guid (TAC09KB_E00392) # entity->name Vertex->name (possibly
 * normalized) # fact:fullname Vertex->name # fact:??? RawKeyValuePair #
 * fact:name RawKeyValuePair->key # fact->{TEXT} RawKeyValuePair->value
 * 
 * Vertex UUID uuid VertexKindAttribute kind => {PERSON, ORGANIZATION, GPE,
 * UNKNOWN} StringAttribute name* UUID uuid AttributeMetadata metadata string
 * tool double timestamp float confidence string value StringAttribute comment*
 * CommunicationGUIDAttribute communication_guid* UUID uuid AttributeMetadata
 * metadata CommunicationGUID value
 * 
 * Communication CommunicationGUID guid string corpus_name (TAC09KB) string
 * communication_id (E000008) UUID uuid string text LanguageIdentification
 * language_id UUID uuid AnnotationMetadata metadata? LanguageProb language*
 * string language (ISO 639-3) float probability
 */

public class TAC09KB2Concrete {

    private static final Logger logger = LoggerFactory.getLogger(TAC09KB2Concrete.class);

    static final AttributeMetadata attribute_metadata;
    static final AnnotationMetadata annotation_metadata;
    static final LanguageProb language_prob;
    static final LanguageIdentification language_id;
    static final double timestamp = (double) ((new Date()).getTime());

    static {
        attribute_metadata = AttributeMetadata.newBuilder().setConfidence(1.0f).setTool("TAC09KB2Concrete.java").setTimestamp(timestamp)
                .build();
        annotation_metadata = AnnotationMetadata.newBuilder().setConfidence(1.0f).setTool("TAC09KB2Concrete.java").setTimestamp(timestamp)
                .build();
        language_prob = LanguageProb.newBuilder().setLanguage("eng").setProbability(1.0f).build();
        language_id = LanguageIdentification.newBuilder().addLanguage(language_prob).setUuid(IdUtil.generateUUID())
                .setMetadata(annotation_metadata).build();
    }

    static final Pattern whitespace_pattern = Pattern.compile("\\s+");

    private Path outputPath;
    private Path commsPath;
    private Path verticesPath;
    private Path idPath;
    private Path namePath;
    private FileWriter idWriter;
    private FileWriter nameWriter;
    private FileOutputStream commPbw;
    private FileOutputStream vertPbw;

    public TAC09KB2Concrete(String pathToOutputFiles) throws ConcreteException {
        this.outputPath = Paths.get(pathToOutputFiles);
        this.commsPath = this.outputPath.resolve("communications.pb");
        this.verticesPath = this.outputPath.resolve("vertices.pb");
        this.idPath = this.outputPath.resolve("ids.txt");
        this.namePath = this.outputPath.resolve("names.txt");

        FileUtil.deleteFolderAndSubfolders(this.commsPath);

        try {
            Files.createDirectories(this.outputPath);
            Files.createFile(this.commsPath);
            Files.createFile(this.verticesPath);
            Files.createFile(this.idPath);
            Files.createFile(this.namePath);

            this.commPbw = new FileOutputStream(this.commsPath.toFile());

            this.vertPbw = new FileOutputStream(this.verticesPath.toFile());
            this.idWriter = new FileWriter(this.idPath.toFile(), true);
            this.nameWriter = new FileWriter(this.namePath.toFile(), true);
        } catch (IOException e) {
            throw new ConcreteException(e);
        }
    }

    public void close() throws ConcreteException {
        try {
            this.commPbw.close();
            this.vertPbw.close();
            this.idWriter.close();
            this.nameWriter.close();
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
        return edu.jhu.hlt.concrete.util.IdUtil.fromJavaUUID(java.util.UUID.nameUUIDFromBytes(("Convert TAC ID to UUID: " + tac_id)
                .getBytes()));
    }

    class KBHandler extends DefaultHandler {

        private TAC09KBEntity currentEntity = null;
        private String factNameKey;
        private String currentText;
        private java.util.Map<String, String> otherAttributes = new java.util.HashMap<String, String>();

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

        public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes attributes) throws SAXException {
            if (qualifiedName.equals("entity")) {
                otherAttributes = new java.util.HashMap<String, String>();
                String idStr = attributes.getValue("id");
                try {
                    idWriter.write(idStr + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.currentEntity = new TAC09KBEntity(idStr);
                this.currentEntity.setKind(Vertex.Kind.UNKNOWN);
                String tac_kind = attributes.getValue("type");
                if (tac_kind.equals("PER"))
                    this.currentEntity.setKind(Vertex.Kind.PERSON);
                else if (tac_kind.equals("ORG"))
                    this.currentEntity.setKind(Vertex.Kind.ORGANIZATION);
                else if (tac_kind.equals("GPE"))
                    this.currentEntity.setKind(Vertex.Kind.GPE);
                this.currentEntity.setName(attributes.getValue("name"));
                // report("wiki_title", attributes.getValue("wiki_title"));
            } else if (qualifiedName.equals("fact")) {
                // collect_text("fact");
                // fact_name = attributes.getValue("name");
                String factName = attributes.getValue("name");
                this.factNameKey = factName;
            }
            // else if (qualifiedName.equals("link")) {
            // ignore links for now.
            // current_link = attributes.getValue("entity_id");
            // }
        }

        public void endElement(String uri, String localName, String qualifiedName) {
            if (qualifiedName.equals("wiki_text")) {
                // Contains the body of the Wikipedia article
                String commText = this.currentText;
                UUID uuid = IdUtil.generateUUID();
                CommunicationGUID guid = ProtoFactory.generateCommGuid("TAC_KB_09", this.currentEntity.getEntityId());
                Communication communication = Communication.newBuilder().setUuid(uuid).setGuid(guid).setText(commText)
                        .addLanguageId(language_id).setKind(Communication.Kind.WIKIPEDIA)
                        .setKnowledgeGraph(KnowledgeGraph.newBuilder().setUuid(IdUtil.generateUUID()).build()).build();
                this.currentEntity.setCommGuid(guid);
                logger.debug("Write conversation for {}", guid.getCommunicationId());
                try {
                    communication.writeDelimitedTo(commPbw);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (qualifiedName.equals("entity")) {
                Vertex.Builder vb = Vertex.newBuilder();
                vb.setDataSetId(this.currentEntity.getEntityId());
                vb.addKind(VertexKindAttribute.newBuilder().setValue(this.currentEntity.getKind()).setMetadata(attribute_metadata)
                        .setUuid(IdUtil.generateUUID()));
                String name = this.currentEntity.getName();
                vb.addName(StringAttribute.newBuilder().setValue(name).setMetadata(attribute_metadata).setUuid(IdUtil.generateUUID()));
                for (Entry<String, String> entry : this.currentEntity.getFactToTextMap().entrySet()) {
                    // currently do nothing..
                }
                for (String key : this.otherAttributes.keySet())
                    vb.addOtherAttributes(Concrete.LabeledAttribute.newBuilder().setUuid(IdUtil.generateUUID())
                            .setMetadata(attribute_metadata).setLabel(key).setValue(this.otherAttributes.get(key)).build());

                // add the comm to the vertex if we have it.
                CommunicationGUID guid = this.currentEntity.getCommGuid();
                CommunicationGUIDAttribute attr = CommunicationGUIDAttribute.newBuilder().setValue(guid).setMetadata(attribute_metadata)
                        .setUuid(IdUtil.generateUUID()).build();
                if (guid != null) {
                    vb.addCommunicationGuid(attr);
                }

                Vertex vertex = vb.setUuid(IdUtil.generateUUID()).build();
                logger.debug("Write vertex for {}", this.currentEntity.getEntityId());
                try {
                    vertex.writeDelimitedTo(vertPbw);
                    nameWriter.write(name + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (qualifiedName.equals("fact")) {
                logger.debug("Current text: {}", this.currentText);
                if (this.factNameKey.equals("fullname"))
                    this.currentEntity.setName(this.currentText);
                else
                    this.otherAttributes.put(this.factNameKey, this.currentText);

            } else if (qualifiedName.equals("link")) {
                // current_link = null;
            }
        }

        public void endDocument() throws SAXException {
        }

        public void characters(char[] chars, int start, int length) {
            this.currentText = new String(chars, start, length);
        }
    }

    static public void main(String[] argv) {
        if (argv.length != 2) {
            logger.error("Usage: java TAC09KB2Concrete <tac09-wp-xmlfile> <output-dir>");
            System.exit(1);
        }

        Path kbPath = Paths.get(argv[0]);
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            TAC09KB2Concrete transducer = new TAC09KB2Concrete(argv[1]);
            KBHandler kbhandler = transducer.new KBHandler();

            // if we're given a list of files (e.g., TAC KB 09 dir), recurse
            // thru them all.
            if (Files.isDirectory(Paths.get(argv[0]))) {
                DirectoryStream<Path> ds = Files.newDirectoryStream(kbPath);
                for (Path p : ds) {
                    if (p.toString().endsWith(".xml")) {
                        InputStream xmlInput = new FileInputStream(p.toFile());
                        saxParser.parse(xmlInput, kbhandler);
                        xmlInput.close();
                    } else {
                        throw new RuntimeException("Path " + p.toString() + " did not point to an xml file.");
                    }
                }
            } else {
                InputStream xmlInput = new FileInputStream(argv[0]);
                saxParser.parse(xmlInput, kbhandler);
                xmlInput.close();
            }
            transducer.close();

        } catch (Exception e) {
            logger.error("Caught exception while running ingest.", e);
        }
    }

}
