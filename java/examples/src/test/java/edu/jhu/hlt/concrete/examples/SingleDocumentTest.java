/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.examples;

import static org.junit.Assert.*;

import java.util.UUID;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.CommunicationType;

/**
 * Simple document serialization/deserialization example with UTF-8 and {@link TBinaryProtocol}.
 * 
 * @author max
 */
public class SingleDocumentTest {
  
  private static final Logger logger = LoggerFactory.getLogger(SingleDocumentTest.class);
  
  @Test
  public void exampleCommunication() throws Exception {
    // create object
    Communication c = new Communication();
    
    // dot and method based setters available
    c.uuid = UUID.randomUUID().toString();
    c.id = "Random-doc-1";
    c.setText("Lốrem ipsum");
    c.type = CommunicationType.OTHER;
    
    byte[] bytez = new TSerializer(new TBinaryProtocol.Factory()).serialize(c);
    TDeserializer deser = new TDeserializer(new TBinaryProtocol.Factory());
    
    // create empty object to deserialize into
    Communication deserialized = new Communication();
    deser.deserialize(deserialized, bytez);
    
    // dot and getter methods available for field access
    assertEquals("UUIDs should be equal.", c.uuid, deserialized.getUuid());
    logger.info("UUID: " + deserialized.getUuid());
    
    assertEquals("Text should be equal.", c.text, deserialized.getText());
    logger.info("Text: " + deserialized.text);
    
    assertEquals("Objects should be equal.", c, deserialized);
  }
}
