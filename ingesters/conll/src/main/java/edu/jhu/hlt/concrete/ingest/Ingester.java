package edu.jhu.hlt.concrete.ingest;

import java.io.File;

import edu.jhu.hlt.concrete.Communication;

/**
 * TODO Change ingest to be 0-args. Ingesters will all need different
 * amounts of setup, which should be put into the constructors. This will allow
 * writing small ingesters and then chaining them together as needed.
 *
 * @author travis
 */
public interface Ingester {

  // TODO Make 0-arg
  public Iterable<Communication> ingest(File f);

}
