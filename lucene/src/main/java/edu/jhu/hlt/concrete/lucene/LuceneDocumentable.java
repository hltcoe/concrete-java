/*
 *
 */
package edu.jhu.hlt.concrete.lucene;

import org.apache.lucene.document.Document;

/**
 * An interface that represents objects that can be
 * inserted into a Lucene index via returning a {@link Document}
 * object.
 */
public interface LuceneDocumentable {
  /**
   * @return the {@link Document} object that represents this object
   */
  public Document getDocument();
}
