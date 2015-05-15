/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package concrete.util;

import java.util.Map;

import edu.jhu.hlt.concrete.Token;
import edu.jhu.hlt.concrete.Tokenization;
import edu.jhu.hlt.concrete.UUID;

/**
 * @deprecated
 */
@Deprecated
public interface ConcreteTokenized extends ConcreteSentenced {
  public Map<UUID, Tokenization> generateTokenizationIdToTokenizationMap();
  public Map<UUID, Map<Integer, Token>> generateTokenizationIdToTokenIdxToTokenMap();
}
