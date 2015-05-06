/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.miscommunication.sectioned;

import java.util.List;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.miscommunication.WrappedCommunication;


/**
 * Interface representing a Concrete {@link Communication} with <strong>at
 * least one</br> section.
 */
public interface SectionedCommunication extends WrappedCommunication {
  public List<Section> getSections();
}
