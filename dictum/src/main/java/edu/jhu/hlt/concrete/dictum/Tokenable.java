/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

/**
 * An interface for objects that can produce {@link Token}s.
 */
public interface Tokenable {
  public Token getToken();
}
