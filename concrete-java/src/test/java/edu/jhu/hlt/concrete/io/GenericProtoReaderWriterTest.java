/**
 * Created on Jun 3, 2013 by thomamj1 <max.thomas@jhuapl.edu>
 */
package edu.jhu.hlt.concrete.io;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.CommunicationGUID;
import edu.jhu.hlt.concrete.Concrete.KnowledgeGraph;
import edu.jhu.hlt.concrete.util.ProtoFactory;

/**
 * @author thomamj1
 *
 */
public class GenericProtoReaderWriterTest {

	ProtoFactory pf = new ProtoFactory();
    CommunicationGUID guidOne = pf.generateMockCommGuid();
    KnowledgeGraph kg = pf.generateMockKnowledgeGraph();
    Communication commOne = Communication
            .newBuilder(ProtoFactory.generateCommunication(guidOne, kg))
            .setText("Sample test text for testing")
            .build();
    List<String> tokensCommOne = new ArrayList<>();
    List<String> tokensCommTwo = new ArrayList<>();

    CommunicationGUID guidTwo = pf.generateMockCommGuid();
    KnowledgeGraph kgTwo = pf.generateMockKnowledgeGraph();
    Communication commTwo = Communication
            .newBuilder(ProtoFactory.generateCommunication(guidTwo, kgTwo))
            .setText("This is test text")
            .build();
    
    private GenericProtoWriter gpw;
    private String pathString = "target/test-output-gpw.pb";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.gpw = new GenericProtoWriter(pathString);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		this.gpw.close();
	}

	/**
	 * Test method for {@link edu.jhu.hlt.concrete.io.GenericProtoWriter#writeToFile(com.google.protobuf.Message)}.
	 * @throws IOException 
	 */
	@Test
	public void testWriteToFile() throws IOException {
		this.gpw.writeToFile(commOne);
		this.gpw.writeToFile(commTwo);
		
		GenericProtoReader gpr = new GenericProtoReader(pathString);
		Set<Communication> commSet = gpr.readAllMessages(commOne);
		assertEquals(2, commSet.size());
	}
}
