/**
 * Created on Jun 3, 2013 by thomamj1 <max.thomas@jhuapl.edu>
 */
package edu.jhu.hlt.concrete.kb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.Vertex;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.kb.TAC09KB2Concrete.KBHandler;

/**
 * @author thomamj1
 *
 */
public class LoadConcreteTACKBFilesTest {

    private static final Logger logger = LoggerFactory
	.getLogger(LoadConcreteTACKBFilesTest.class);

    private LoadConcreteTACKBFiles loader;
    private String dataPath = "target/test-inandout";
    private Path commsPath = Paths.get(dataPath).resolve("communications.pb");
    private Path vertPath = Paths.get(dataPath).resolve("vertices.pb");
    private Path idPath = Paths.get(dataPath).resolve("ids.txt");
    private Path namePath = Paths.get(dataPath).resolve("names.txt");
    private String kbPath = "src/test/resources/test-kb-folder";
	
	
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	this.loader = new LoadConcreteTACKBFiles(dataPath);
	Files.deleteIfExists(commsPath);
	Files.deleteIfExists(vertPath);
	Files.deleteIfExists(idPath);
	Files.deleteIfExists(namePath);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
	Files.delete(commsPath);
	Files.delete(vertPath);
	Files.delete(idPath);
	Files.delete(namePath);
    }
	
    private void ingestDemoFiles() throws ConcreteException {
	try {
	    Path kbP = Paths.get(kbPath);
			
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser saxParser = factory.newSAXParser();
	    TAC09KB2Concrete transducer = new TAC09KB2Concrete(this.dataPath);
	    KBHandler kbhandler = transducer.new KBHandler();
			
	    // if we're given a list of files (e.g., TAC KB 09 dir), recurse thru them all. 
	    if (Files.isDirectory(Paths.get(this.dataPath))) {
		DirectoryStream<Path> ds = Files.newDirectoryStream(kbP);
		for (Path p : ds) {
		    if (p.toString().endsWith(".xml")) {
			InputStream xmlInput = new FileInputStream(p.toFile());
			saxParser.parse(xmlInput, kbhandler);
			xmlInput.close();
		    } else {
			throw new RuntimeException("Path " + p.toString() + " did not point to an xml file.");
		    }
		}
	    }
			
	    transducer.close();
	} catch (ParserConfigurationException | SAXException | ConcreteException | IOException e) {
	    throw new ConcreteException(e);
	} 
            
    }

    /**
     * Test method for {@link edu.jhu.hlt.concrete.kb.LoadConcreteTACKBFiles#loadVertices()}.
     * @throws ConcreteException 
     */
    @Test
    public void testLoadVertices() throws ConcreteException {
	this.ingestDemoFiles();
	Set<Vertex> vertexSet = loader.loadVertices();
	logger.info("Got " + vertexSet.size() + " vertices.");
    }
	
	

    /**
     * Test method for {@link edu.jhu.hlt.concrete.kb.LoadConcreteTACKBFiles#loadCommunications()}.
     * @throws ConcreteException 
     */
    @Test
    public void testLoadCommunications() throws ConcreteException {
	this.ingestDemoFiles();
	Set<Communication> vertexSet = loader.loadCommunications();
	logger.info("Got " + vertexSet.size() + " communications.");
    }

}
