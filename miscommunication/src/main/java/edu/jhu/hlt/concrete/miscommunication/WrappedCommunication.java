/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication;

import edu.jhu.hlt.concrete.Communication;

/**
 * Interface that represents types that can be converted to Concrete
 * {@link Communication} objects.
 */
public interface WrappedCommunication {
  /**
   * @return a {@link Communication} representing the original object
   */
  public Communication getRoot();
}
