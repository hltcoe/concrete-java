/**
 * Created on Jun 3, 2013 by thomamj1 <max.thomas@jhuapl.edu>
 */
package edu.jhu.hlt.concrete.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.protobuf.Message;

/**
 * @author thomamj1
 *
 */
public class GenericProtoWriter {

	private final Path pathToPbFile;
	private final FileOutputStream fos;
	
	public GenericProtoWriter(String path) throws FileNotFoundException {
		this.pathToPbFile = Paths.get(path);
		this.fos = new FileOutputStream(this.pathToPbFile.toFile());
	}
	
	public <T extends Message> void writeToFile(T type) throws IOException {
		type.writeDelimitedTo(this.fos);
	}
	
	public void close() throws IOException {
		this.fos.close();
	}
}
