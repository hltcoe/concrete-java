/**
 * Created on Jun 3, 2013 by thomamj1 <max.thomas@jhuapl.edu>
 */
package edu.jhu.hlt.concrete.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;

/**
 * @author thomamj1
 *
 */
public class GenericProtoReader {
	private final Path pathToPbFile;
	private final FileInputStream fis;
	
	public GenericProtoReader (String pathToPbFile) throws FileNotFoundException {
		this.pathToPbFile = Paths.get(pathToPbFile);
		this.fis = new FileInputStream(this.pathToPbFile.toFile());
	}
	
	public <T extends Message> Set<T> readAllMessages(T type) throws IOException {
		Set<T> set = new HashSet<T>();
		while (fis.available() != 0) {
			Parser<? extends Message> parser = type.getParserForType();
			Message m = parser.parseDelimitedFrom(fis);
			// this will throw if you pass in a type that isn't compatible with what's written
			T element = (T) type.getClass().cast(m);
			set.add(element);
		}
		
		return set;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
