package edu.jhu.hlt.concrete.zip;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * @author Tongfei Chen
 */
interface ImmutableSet<A> extends Set<A> {

    default int size() {
        int n = 0;
        Iterator<A> it = iterator();
        while (it.hasNext()) {
            it.next();
            n += 1;
        }
        return n;
    }

    default boolean isEmpty() {
        return iterator().hasNext();
    }

    default Iterable<A> elements() {
        return () -> iterator();
    }

    default Object[] toArray() {
        return StreamSupport.stream(elements().spliterator(), false).toArray();
    }

    default <T> T[] toArray(T[] a) {
        if (a.length >= size()) {
            Iterator<A> it = iterator();
            int i = 0;
            while (it.hasNext()) {
                a[i] = (T) it.next();
                i += 1;
            }
            return a;
        }
        else return (T[]) toArray();
    }

    default boolean add(A a) {
        throw new UnsupportedOperationException("Immutable map");
    }

    default boolean remove(Object o) {
        throw new UnsupportedOperationException("Immutable map");
    }

    default boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(x -> contains(x));
    }

    default boolean addAll(Collection<? extends A> c) {
        throw new UnsupportedOperationException("Immutable map");
    }

    default boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Immutable map");
    }

    default boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Immutable map");
    }

    default void clear() {
        throw new UnsupportedOperationException("Immutable map");
    }
}
