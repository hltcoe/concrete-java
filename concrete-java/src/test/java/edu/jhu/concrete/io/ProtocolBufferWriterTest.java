package edu.jhu.concrete.io;

//import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.concrete.Concrete.Communication;
import edu.jhu.concrete.util.IdUtil;
import edu.jhu.concrete.util.ProtoFactory;

public class ProtocolBufferWriterTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ProtocolBufferWriterTest.class);
    
    @Before
    public void setUp() throws Exception {
        File outputFolder = new File("target");
        if (!outputFolder.exists())
            outputFolder.mkdir();
    }

    @After
    public void tearDown() throws Exception {
        File output = new File("target/test-out.pb");
        if (output.exists())
            output.delete();
    }

    @Test
    public void testWrite() throws Exception {
        File output = new File("target/test-out.pb");
        FileOutputStream fos = new FileOutputStream(output);
        Communication comm = new ProtoFactory().generateMockCommunication();
        
        logger.info("Writing ID: " + IdUtil.uuidToString(comm.getUuid()));
        logger.info("Writing GUID: " + comm.getGuid().getCommunicationId());
        ProtocolBufferWriter pbw = new ProtocolBufferWriter(fos);
        pbw.write(comm);
        
        FileInputStream fis = new FileInputStream(output);
        ProtocolBufferReader pbr = new ProtocolBufferReader(fis, Communication.class);
        Communication readComm = (Communication) pbr.next();
        
        logger.info("Got a communication: " + IdUtil.uuidToString(readComm.getUuid()));
        logger.info("Got GUID: " + readComm.getGuid().getCommunicationId());
    }

}
