/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.ingesters.base.communications;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Interface that represents types that can be converted to Concrete
 * {@link Communication} objects.
 */
public interface Communicationizable {
  /**
   * @return a {@link Communication} representing the original object
   * @throws ConcreteException if there is an error with the conversion
   */
  public Communication toCommunication() throws ConcreteException;
}
