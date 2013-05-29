/**
 * 
 */
package edu.jhu.hlt.concrete.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.CommunicationGUID;
import edu.jhu.hlt.concrete.Concrete.KnowledgeGraph;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.io.ProtocolBufferReader;
import edu.jhu.hlt.concrete.io.ProtocolBufferWriter;

/**
 * Utility class for easily generating various protocol buffer objects in the
 * {@link Concrete} class.
 * 
 * @author max
 *
 */
public class ProtoFactory {

    private final Random r;
    
    /**
     * Generated an {@link ProtoFactory} class with a seed for randomly generated data.
     */
    public ProtoFactory(long seed) {
        this.r = new Random(seed);
    }
    
    
    public ProtoFactory() {
        this.r = new Random();
    }
    
    /////////////////////////////////////////////
    // Instance methods
    /////////////////////////////////////////////
    
    /**
     * Generate a "mock" {@link CommunicationGUID}, suitable for testing.
     * 
     * @return a {@link CommunicationGUID} object with random data
     */
    public CommunicationGUID generateMockCommGuid() {
        return generateCommGuid("Corpus" + r.nextInt(1000), "Communication" + r.nextInt(1000000));
    }
    
    /**
     * Generate a "mock" {@link KnowledgeGraph}, suitable for testing.
     * 
     * @return a {@link KnowledgeGraph} object with random data
     */
    public KnowledgeGraph generateMockKnowledgeGraph() {
        return generateKnowledgeGraph();
    }
    
    /**
     * Generate a "mock" {@link Communication}, suitable for testing.
     * 
     * @return a {@link Communication} object with random data
     */
    public Communication generateMockCommunication() {
        return generateCommunication(generateMockCommGuid(), generateMockKnowledgeGraph());
    }
    
    /////////////////////////////////////////////
    // Static methods
    /////////////////////////////////////////////
    
    /**
     * Generate a {@link CommunicationGUID} object.
     * 
     * @param corpusName - name of the corpus
     * @param commId - id of the {@link Communication}
     * @return a {@link CommunicationGUID} object
     */
    public static CommunicationGUID generateCommGuid(String corpusName, String commId) {
        return CommunicationGUID.newBuilder()
                .setCorpusName(corpusName)
                .setCommunicationId(commId)
                .build();
    }
    
    /**
     * Generate a {@link KnowledgeGraph} object.
     * 
     * @return a {@link KnowledgeGraph} object
     */
    public static KnowledgeGraph generateKnowledgeGraph() {
        return KnowledgeGraph.newBuilder()
                .setUuid(IdUtil.generateUUID())
                .build();
    }
    
    /**
     * Generate a {@link Communication} object. 
     * 
     * @param guid - the {@link CommunicationGUID} object to use for this {@link Communication}
     * @param graph - the {@link KnowledgeGraph} object to use for this {@link Communication}
     * @return a {@link Communication} object
     */
    public static Communication generateCommunication(CommunicationGUID guid, KnowledgeGraph graph) {
        return Communication.newBuilder()
                .setGuid(guid)
                .setKnowledgeGraph(graph)
                .setUuid(IdUtil.generateUUID())
                .build();
    }
    
    public static void writeCommunication(Communication c, Path outputPath)
            throws ConcreteException {
        try {
            File commFile = outputPath.toFile();
            FileOutputStream fos = new FileOutputStream(commFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ProtocolBufferWriter pbw = new ProtocolBufferWriter(bos);
            pbw.write(c);
            pbw.close();
        } catch (IOException e) {
            throw new ConcreteException(e);
        }
    }

    public static Communication readCommunicationFromPath(Path pathToComm)
            throws ConcreteException {
        try {
            File commFile = pathToComm.toFile();
            FileInputStream fis = new FileInputStream(commFile);
            ProtocolBufferReader pbr = new ProtocolBufferReader(fis,
                    Communication.class);
            Communication c = (Communication) pbr.next();

            pbr.close();
            fis.close();

            return c;
        } catch (IOException e) {
            throw new ConcreteException(e);
        }
    }
    
    private static final Communication buildCommunication (Communication.Builder commBuilder) {
        return commBuilder.build();
    }
}