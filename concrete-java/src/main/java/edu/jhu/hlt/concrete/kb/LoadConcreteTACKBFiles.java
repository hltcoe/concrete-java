/**
 * 
 */
package edu.jhu.hlt.concrete.kb;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.Vertex;
import edu.jhu.hlt.concrete.ConcreteException;

/**
 * @author max
 *
 */
public class LoadConcreteTACKBFiles {

    private static final Logger logger = LoggerFactory
            .getLogger(LoadConcreteTACKBFiles.class);
    
    private final Path pathOnDisk;
    private final Path commsPath;
    private final Path verticesPath;
    
    /**
     * 
     */
    public LoadConcreteTACKBFiles(Path pathOnDisk) {
        this.pathOnDisk = pathOnDisk;
        this.commsPath = this.pathOnDisk.resolve("communications.pb");
        this.verticesPath = this.pathOnDisk.resolve("vertices.pb");
    }
    
    public LoadConcreteTACKBFiles(String pathOnDisk) {
        this(Paths.get(pathOnDisk));
    }
    
    public Set<Vertex> loadVertices() throws ConcreteException {
        try {
            Set<Vertex> vertexSet = new HashSet<>();
            FileInputStream fis = new FileInputStream(this.verticesPath.toFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            while (bis.available() != 0) {
            	Vertex v = Vertex.PARSER.parseDelimitedFrom(bis);
            	vertexSet.add(v);
            	logger.debug("Got vertex: " + v.getDataSetId());
                logger.debug("Got name: " + v.getNameList().get(0).getValue());
            }

            fis.close();
            return vertexSet;
        } catch (IOException e) {
            throw new ConcreteException(e);
        }
    }
    
    public Set<Communication> loadCommunications() throws ConcreteException {
        try {
            Set<Communication> commSet = new HashSet<>();
            FileInputStream fis = new FileInputStream(this.commsPath.toFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            while (bis.available() != 0) {
            	Communication c = Communication.PARSER.parseDelimitedFrom(bis);
            	commSet.add(c);
            	logger.debug("Got vertex: " + c.getGuid().getCommunicationId());
            }

            fis.close();
            return commSet;
        } catch (IOException e) {
            throw new ConcreteException(e);
        }
    }
    
    public static void main(String... args) throws ConcreteException {
    	if (args.length != 1) {
    		logger.error("Usage: LoadConcreteTACKBFiles <path/to/root/data/dir>");
    		System.exit(1);
    	}
    	
    	LoadConcreteTACKBFiles loader = new LoadConcreteTACKBFiles(args[0]);
    	long millis = System.currentTimeMillis();
    	Set<Communication> commSet = loader.loadCommunications();
    	logger.info("Got " + commSet.size() + " communications.");
    	logger.info("Took: " + (System.currentTimeMillis() - millis) + " ms to load the comms.");
    	millis = System.currentTimeMillis();
    	Set<Vertex> vertSet = loader.loadVertices();
    	logger.info("Got " + vertSet.size() + " vertices.");
    	logger.info("Took: " + (System.currentTimeMillis() - millis) + " ms to load the vertices.");
    }
}
