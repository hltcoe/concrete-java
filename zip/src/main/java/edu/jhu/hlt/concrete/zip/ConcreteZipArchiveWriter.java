package edu.jhu.hlt.concrete.zip;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A writer than supports writing Concrete communications to a zip archive.
 * @author Tongfei Chen
 * @since 4.12.1
 */
public class ConcreteZipArchiveWriter implements Closeable {

    ZipArchiveOutputStream zaos;

    public ConcreteZipArchiveWriter(Path path) throws IOException {
        zaos = new ZipArchiveOutputStream(new BufferedOutputStream(Files.newOutputStream(path)));
    }

    public ConcreteZipArchiveWriter(String filename) throws IOException {
        this(Paths.get(filename));
    }

    public void close() throws IOException {
        zaos.close();
    }

    public void write(Communication comm) throws IOException, ConcreteException {
        zaos.putArchiveEntry(new ZipArchiveEntry(comm.getId() + ".concrete"));
        zaos.write(Util.ser.toBytes(comm));
        zaos.closeArchiveEntry();
    }

}
