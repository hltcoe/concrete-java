/*
 *
 */
package edu.jhu.hlt.concrete.lucene;

import java.io.IOException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.miscommunication.MiscCommunication;

/**
 * An interface that represents an object that is capable
 * of indexing Concrete {@link Communication} objects.
 */
public interface LuceneCommunicationIndexer extends AutoCloseable {

  /**
   * @param mc a {@link MiscCommunication} to index
   * @throws IOException on failure to add the document
   */
  public void add(MiscCommunication mc) throws IOException;

  /**
   * Commits the index.
   *
   * @throws IOException
   */
  public void commit() throws IOException;

  /**
   * @param c the {@link Communication} to index
   * @throws IOException on failure to add the document
   *
   * @see #add(MiscCommunication)
   */
  default public void add(Communication c) throws IOException {
    this.add(MiscCommunication.create(c));
  }

  /**
   * Close the index.
   */
  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws IOException;
}