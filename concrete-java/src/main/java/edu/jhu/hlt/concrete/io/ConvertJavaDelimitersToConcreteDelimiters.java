package edu.jhu.hlt.concrete.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;

import edu.jhu.hlt.concrete.Concrete.Discourse;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.Vertex;
import edu.jhu.hlt.concrete.kb.LoadConcreteTACKBFiles;

/**
 * ProtoBuffers provide a method for storing multiple protobufs in the same file.
 * However, this method only works in Java.
 * In contrast, the Concrete Delim format is cross platform.
 * This class converts the Java file format to the Concrete file format.
 * @author Mark Dredze
 *
 */
public class ConvertJavaDelimitersToConcreteDelimiters {

	private static final Logger logger = LoggerFactory.getLogger(LoadConcreteTACKBFiles.class);
	 
    public static void main(String... args) throws ConcreteException, com.google.protobuf.InvalidProtocolBufferException, IOException {
        if (args.length != 3) {
            logger.error("Usage: ConvertJavaDelimitersToConcreteDelimiters <communication|vertex|discourse> <javaDelimFile> <concreteDelimFile>");
            System.exit(1);
        }

        ConvertJavaDelimitersToConcreteDelimiters converter = new ConvertJavaDelimitersToConcreteDelimiters();
        String objectType = args[0];
        String inputFile = args[1];
        String outputFile = args[2];
        
        logger.info("Converting from " + inputFile+ " to " + outputFile);
        converter.convert(inputFile, outputFile, objectType);
        
    }

	private void convert(String inputFile, String outputFile, String objectType) throws com.google.protobuf.InvalidProtocolBufferException, IOException {
        InputStream fis = new FileInputStream(new File(inputFile));
        if (inputFile.endsWith(".gz"))
        	fis = new GZIPInputStream(fis);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ProtocolBufferWriter writer = new ProtocolBufferWriter(outputFile); 
        int numMessages = 0;
        while (bis.available() != 0) {
            Message message = null;
            if (objectType.equalsIgnoreCase("communication"))
            	message = Communication.PARSER.parseDelimitedFrom(bis);
            else if (objectType.equalsIgnoreCase("vertex"))
            	message = Vertex.PARSER.parseDelimitedFrom(bis);
            else if (objectType.equalsIgnoreCase("discourse"))
            	message = Discourse.PARSER.parseDelimitedFrom(bis);
            else
            	throw new IllegalArgumentException("Invalid message type: " + objectType);
            
            writer.write(message);
            numMessages ++;
        }

        bis.close();
        writer.close();
        logger.info("Converted " + numMessages + " messages.");
		
	}
}
