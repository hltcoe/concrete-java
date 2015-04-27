/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.safe.section;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.utilt.uuid.UUIDable;

/**
 * Class representing a Concrete {@link Section} with required fields.
 */
public interface SafeSection extends UUIDable {
  /**
   * @return the type of {@link Section}
   */
  public String getKind();
}
