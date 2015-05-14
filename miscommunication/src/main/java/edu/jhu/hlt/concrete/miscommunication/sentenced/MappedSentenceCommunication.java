/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sentenced;

import java.util.Map;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.UUID;

/**
 * Interface exposing a method to allow consumers to obtain a 
 * {@link UUID}, {@link Sentence} map for a given Concrete
 * {@link Communication}.
 */
public interface MappedSentenceCommunication extends SentencedCommunication {
  public Map<UUID, Sentence> getUuidToSentenceMap();
}
