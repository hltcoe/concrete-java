package edu.jhu.hlt.concrete.io;

//import static org.junit.Assert.fail;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.CommunicationGUID;
import edu.jhu.hlt.concrete.Concrete.UUID;
import edu.jhu.hlt.concrete.util.ProtoFactory;

public class ProtocolBufferWriterTest {
    
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
        
        UUID id = comm.getUuid();
        CommunicationGUID guid = comm.getGuid();
        ProtocolBufferWriter pbw = new ProtocolBufferWriter(fos);
        pbw.write(comm);
        
        FileInputStream fis = new FileInputStream(output);
        ProtocolBufferReader pbr = new ProtocolBufferReader(fis, Communication.class);
        Communication readComm = (Communication) pbr.next();
        
        UUID readId = readComm.getUuid();
        CommunicationGUID readGuid = readComm.getGuid();
        assertEquals(id, readId);
        assertEquals(guid, readGuid);
        
        pbr.close();
    }

}
