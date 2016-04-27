/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.data;

/**
 * Small interface that acts as an minimum description for underlying
 * documents that might be transformed to Concrete.
 */
public interface Documentable {
  /**
   * @return a corpus-specific identifier for the underlying document.
   */
  public String getId();

  /**
   * @return the representation of the underlying document as a byte array.
   */
  public byte[] getBytes();

  /**
   * @return the character encoding of the underlying document.
   */
  public String getEncoding();

  /**
   * @return the corpus associated with the underlying document
   */
  public String getCorpusName();

  /**
   * @return the path, on disk, where this document (or archive containing it)
   * exists.
   */
  public String getPath();

  /**
   * @return a notional type of this document (tweet, etc.).
   */
  public String getType();
}
