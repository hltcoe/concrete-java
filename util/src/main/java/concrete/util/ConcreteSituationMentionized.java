/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package concrete.util;

import java.util.Map;

import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.UUID;

/**
 * @author max
 *
 */
public interface ConcreteSituationMentionized extends ConcreteTokenized {
  public Map<UUID, SituationMention> generateSituationMentionIdToSituationMentionMap();
}
