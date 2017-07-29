package edu.jhu.hlt.concrete.zip;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOError;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Utility class to process Concrete zip archives.
 * @author Tongfei Chen
 * @since 4.12.0
 */
public class ConcreteZipIO {

    public static Iterable<Communication> read(String fn) {
        try {
            ZipFile zf = new ZipFile(fn);
            return Util.read(zf);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Communication> readAsStream(String fn) {
        try {
            ZipFile zf = new ZipFile(fn);
            return Util.readAsStream(zf);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Communication> openAsMap(String fn) {
        try {
            return new ConcreteZipArchive(fn);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(String fn, Iterable<Communication> comms) {
        try (ConcreteZipArchiveWriter czaw = new ConcreteZipArchiveWriter(fn)) {
            for (Communication comm : comms)
                czaw.write(comm);
        }
        catch (IOException | ConcreteException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(String fn, Stream<Communication> comms) {
        try (ConcreteZipArchiveWriter czaw = new ConcreteZipArchiveWriter(fn)) {
            comms.forEach(comm -> {
                try {
                    czaw.write(comm);
                }
                catch (IOException | ConcreteException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
