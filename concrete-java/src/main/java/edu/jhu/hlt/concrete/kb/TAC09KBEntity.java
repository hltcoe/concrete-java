/**
 * 
 */
package edu.jhu.hlt.concrete.kb;

import java.util.HashMap;
import java.util.Map;

import edu.jhu.hlt.concrete.Concrete.Vertex;

/**
 * @author max
 *
 */
public class TAC09KBEntity {

    private final String entityId;
    private String name;
    private Vertex.Kind kind;
    private final Map<String, String> factToTextMap;
    
    /**
     * 
     */
    public TAC09KBEntity(String entityId) {
        this.entityId = entityId;
        this.factToTextMap = new HashMap<>();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the kind
     */
    public Vertex.Kind getKind() {
        return kind;
    }

    /**
     * @param kind the kind to set
     */
    public void setKind(Vertex.Kind kind) {
        this.kind = kind;
    }

    /**
     * @return the factToTextMap
     */
    public Map<String, String> getFactToTextMap() {
        return factToTextMap;
    }

    /**
     * @return the entityId
     */
    public String getEntityId() {
        return entityId;
    }
    
    public void addFactToTextEntry(String fact, String text) {
        this.factToTextMap.put(fact, text);
    }

}
