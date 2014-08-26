/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package concrete.util.data;

import java.io.InputStream;

/**
 * 2 tuple that wraps a {@link String} and an {@link InputStream}.
 * 
 * @author max
 */
public class StringInputStreamTuple {

  public final String string;
  public final InputStream inputStream;
  
  /**
   * 
   */
  public StringInputStreamTuple(String string, InputStream inputStream) {
    this.string = string;
    this.inputStream = inputStream;
  }
}
