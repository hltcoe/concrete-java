/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * @author max
 *
 */
public interface CommunicationTarGzSerializer extends CommunicationTarSerializer {
  public void toTarGz(Collection<Communication> commColl, Path outPath) throws ConcreteException, IOException;

  public void toTarGz(Collection<Communication> commColl, String outPathString) throws ConcreteException, IOException;

  /**
   * @param is
   *          an {@link InputStream}.You should close this when finished iterating.
   * @return
   * @throws ConcreteException
   */
  public Iterator<Communication> fromTarGz(InputStream is) throws ConcreteException, IOException;
}
