/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.dictum;

import java.util.Optional;

import edu.jhu.hlt.concrete.dictum.primitives.Confidence;

/**
 * Interface for objects that may produce a {@link Confidence}.
 */
public interface ConfidenceScorable {
  public Optional<Confidence> getConfidence();
}
