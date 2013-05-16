/**
 * 
 */
package edu.jhu.concrete.util;

import java.util.Random;

import edu.jhu.concrete.Concrete;
import edu.jhu.concrete.Concrete.Communication;
import edu.jhu.concrete.Concrete.CommunicationGUID;
import edu.jhu.concrete.Concrete.KnowledgeGraph;
import edu.jhu.concrete.util.IdUtil;

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
}