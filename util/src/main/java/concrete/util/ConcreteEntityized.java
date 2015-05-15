/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package concrete.util;

import java.util.Map;

import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.UUID;

/**
 * @deprecated
 */
@Deprecated
public interface ConcreteEntityized extends ConcreteEntityMentionized {
  public Map<UUID, Entity> generateEntityIdToEntityMap();
}
