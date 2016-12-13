package edu.jhu.hlt.concrete.simpleaccumulo;

import java.util.Iterator;

public interface AutoCloseableIterator<T> extends AutoCloseable, Iterator<T> {
}
