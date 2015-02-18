/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.random.RandomConcreteFactory;

/**
 *
 *
 */
public abstract class AbstractValidationTest {

  protected Communication comm;
  protected final RandomConcreteFactory factory;

  /**
   *
   */
  public AbstractValidationTest() {
    this.factory = new RandomConcreteFactory();
    this.comm = this.factory.communication();
  }
}
