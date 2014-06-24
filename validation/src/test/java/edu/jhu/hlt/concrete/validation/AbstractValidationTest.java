/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.ConcreteFactory;

/**
 *
 *
 * @author max
 *
 */
public abstract class AbstractValidationTest {

  protected Communication comm;
  protected final ConcreteFactory factory;

  /**
   *
   */
  public AbstractValidationTest() {
    this.factory = new ConcreteFactory();
    this.comm = this.factory.randomCommunication();
  }
}
