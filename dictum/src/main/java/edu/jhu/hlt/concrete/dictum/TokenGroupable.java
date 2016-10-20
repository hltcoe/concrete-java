/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Optional;
/**
 * Interface whose implementations offer an {@link Optional}
 * {@link FlatTokenGrouping}.
 *
 */
public interface TokenGroupable {
  public Optional<FlatTokenGrouping> getTokenGrouping();
}
