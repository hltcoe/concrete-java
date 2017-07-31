package edu.jhu.hlt.concrete.zip;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Tongfei Chen
 * @since 4.12.0
 */
class Util {

    static CompactCommunicationSerializer ser = new CompactCommunicationSerializer();

    public static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new Iterator<T>() {
                            public T next() {
                                return e.nextElement();
                            }
                            public boolean hasNext() {
                                return e.hasMoreElements();
                            }
                        },
                        Spliterator.ORDERED), false);
    }

    public static Communication read(InputStream is) {
        try {
            byte[] bytes = IOUtils.toByteArray(is);
            return ser.fromBytes(bytes);
        }
        catch (IOException | ConcreteException e) {
            return null;
        }
    }

    public static Communication read(ZipFile zf, ZipArchiveEntry zae) {
        try {
            return read(zf.getInputStream(zae));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Communication> readAsStream(ZipFile zf) {
        return Util.enumerationAsStream(zf.getEntries())
                .filter(e -> e.getName().endsWith(".comm") || e.getName().endsWith(".concrete"))
                .map(e -> {
                    try {
                        return Util.read(zf.getInputStream(e));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }


    public static Iterable<Communication> read(ZipFile zf) {
        return () -> readAsStream(zf).iterator();
    }



}