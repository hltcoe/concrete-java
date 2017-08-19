package edu.jhu.hlt.concrete.zip;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.io.Files;

import edu.jhu.hlt.concrete.Communication;

/**
 * Casts a Concrete zip archive as a map that maps Communication IDs to the actual communications.
 *
 * In order to support random access of Communications by their
 * Communication IDs, Concrete-zip assumes that any Communication
 * files in the zip archive (1) follow the naming convention
 * `[COMMUNICATION_ID].comm` or `[COMMUNICATION_ID].concrete` and (2)
 * are in the root directory of the zip archive
 * @author Tongfei Chen
 * @since 4.12.0
 */ // @tongfei: Java collections suck
public class ConcreteZipArchive implements Map<String, Communication>, Closeable {

    ZipFile zf;

    public ConcreteZipArchive(Path path) throws IOException {
        zf = new ZipFile(path.toAbsolutePath().toString());
    }

    public ConcreteZipArchive(String filename) throws IOException {
        this(Paths.get(filename));
    }

    @Override
    public void close() throws IOException {
        zf.close();
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return !zf.getEntries().hasMoreElements();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) return false;
        else {
            String name = (String) key;
            return (zf.getEntry(name + ".concrete") != null
            || zf.getEntry(name + ".comm") != null);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Complexity too high");
    }

    @Override
    public Communication get(Object key) {
        if (!(key instanceof String)) return null;
        else {
            String name = (String) key;
            ZipArchiveEntry zae = zf.getEntry(name + ".concrete");
            if (zae == null) zae = zf.getEntry(name + ".comm");
            if (zae != null) {
                return Util.read(zf, zae);
            }
            return null;
        }
    }

    @Override
    public Communication put(String key, Communication value) {
        throw new UnsupportedOperationException("Immutable map");
    }

    @Override
    public Communication remove(Object key) {
        throw new UnsupportedOperationException("Immutable map");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Communication> m) {
        throw new UnsupportedOperationException("Immutable map");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Immutable map");
    }

    @Override
    public Set<String> keySet() {
        return new ConcreteZipArchiveKeySet(this);
    }

    @Override
    public Collection<Communication> values() {
        return new ConcreteZipArchiveValueCollection(this);
    }

    @Override
    public Set<Entry<String, Communication>> entrySet() {
        return new ConcreteZipArchiveEntrySet(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

class ConcreteZipArchiveEntrySet implements ImmutableSet<Map.Entry<String, Communication>> {

    ConcreteZipArchive parent;

    ConcreteZipArchiveEntrySet(ConcreteZipArchive parent) {
        this.parent = parent;
    }

    @Override
    public boolean contains(Object o) {
        return parent.containsKey(o);
    }

    // @tongfei: Iterable<T> is not covariant. f**k.
    @Override
    public Iterator<Map.Entry<String, Communication>> iterator() {
        // @tongfei: this type cast is safe because the internal structure is an iterator.
        // it is covariant but stupid java8 cannot infer this
        return (Iterator<Map.Entry<String, Communication>>)(Object)Util.enumerationAsStream(parent.zf.getEntries())
                .filter(Util.CONCRETE_FILE_SUFFIX_PREDICATE)
                .map(e -> new Map.Entry<String, Communication>() {
                    @Override
                    public String getKey() {
                        return Files.getNameWithoutExtension(e.getName());
                    }
                    @Override
                    public Communication getValue() {
                        return Util.read(parent.zf, e);
                    }
                    @Override
                    public Communication setValue(Communication value) {
                        throw new UnsupportedOperationException("Immutable set");
                    }
                }).iterator();
    }

}

class ConcreteZipArchiveKeySet implements ImmutableSet<String> {

    ConcreteZipArchive parent;

    ConcreteZipArchiveKeySet(ConcreteZipArchive parent) {
        this.parent = parent;
    }

    @Override
    public boolean contains(Object o) {
        return parent.containsKey(o);
    }

    @Override
    public Iterator<String> iterator() {
        return Util.enumerationAsStream(parent.zf.getEntries())
                .filter(Util.CONCRETE_FILE_SUFFIX_PREDICATE)
                .map(e -> Files.getNameWithoutExtension(e.getName()))
                .iterator();
    }
}

class ConcreteZipArchiveValueCollection implements Collection<Communication> {

    ConcreteZipArchive parent;

    ConcreteZipArchiveValueCollection(ConcreteZipArchive parent) {
        this.parent = parent;
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Complexity too high");
    }

    @Override
    public Iterator<Communication> iterator() {
        return Util.readAsStream(parent.zf).iterator();
    }

    @Override
    public Object[] toArray() {
        Iterable<Communication> ci = () -> iterator();
        return StreamSupport.stream(ci.spliterator(), false).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length >= size()) {
            Iterator<Communication> it = iterator();
            int i = 0;
            while (it.hasNext()) {
                a[i] = (T) it.next();
                i += 1;
            }
            return a;
        }
        else return (T[]) toArray();
    }

    @Override
    public boolean add(Communication communication) {
        throw new UnsupportedOperationException("Immutable collection");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Immutable collection");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Complexity too high");
    }

    @Override
    public boolean addAll(Collection<? extends Communication> c) {
        throw new UnsupportedOperationException("Immutable collection");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Immutable collection");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Immutable collection");
    }
}
