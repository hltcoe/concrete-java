/**
 * 
 */
package edu.jhu.hlt.concrete.kb;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Concrete.Vertex;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.io.ProtocolBufferReader;

/**
 * @author max
 *
 */
public class LoadConcreteTACKBFiles {

    private static final Logger logger = LoggerFactory
            .getLogger(LoadConcreteTACKBFiles.class);
    
    private final Path pathOnDisk;
    
    /**
     * 
     */
    public LoadConcreteTACKBFiles(Path pathOnDisk) {
        this.pathOnDisk = pathOnDisk;
    }
    
    public LoadConcreteTACKBFiles(String pathOnDisk) {
        this(Paths.get(pathOnDisk));
    }
    
    public Set<Vertex> loadVertices() throws ConcreteException {
        try {
            Set<Vertex> vertexSet = new HashSet<>();
            DirectoryStream<Path> ds = Files
                    .newDirectoryStream(this.pathOnDisk);
            Iterator<Path> pathIter = ds.iterator();
            while (pathIter.hasNext()) {
                Path nextPath = pathIter.next();
                ProtocolBufferReader pbw = 
                        new ProtocolBufferReader(new FileInputStream(nextPath.toFile()), 
                                Vertex.class);
                Vertex v = (Vertex)pbw.next();
                logger.debug("Got vertex: " + v.getDataSetId());
                logger.debug("Got name: " + v.getNameList().get(0).getValue());
                vertexSet.add(v);
                pbw.close();
            }
            return vertexSet;
        } catch (IOException e) {
            throw new ConcreteException(e);
        }
    }

    /**
     * @param args
     * @throws ConcreteException 
     */
    public static void main(String[] args) throws ConcreteException {
        if (args.length != 1) {
            logger.info("Usage: LoadConcreteTACKBFiles /path/to/ingest/dir");
            System.exit(1);
        }
        
        LoadConcreteTACKBFiles loader = new LoadConcreteTACKBFiles(args[0]);
        Set<Vertex> vSet = loader.loadVertices();
        logger.info("Loaded " + vSet.size() + " vertices.");
    }
}
