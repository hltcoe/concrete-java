/**
 *
 */
package edu.jhu.hlt.concrete.storers;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.services.store.StoreTool;
import groovyx.gpars.dataflow.DataflowReadChannel;

/**
 *
 */
public class DataflowStoreReader implements Callable<Void> {

  private static final Logger LOG = LoggerFactory.getLogger(DataflowStoreReader.class);

  private final StoreTool storer;
  private final DataflowReadChannel<Communication> input;

  public DataflowStoreReader(StoreTool storer, DataflowReadChannel<Communication> in) {
    this.storer = storer;
    this.input = in;
  }

  @Override
  public Void call() throws Exception {
    // block until the next communication
    Communication c = this.input.getVal();
    LOG.debug("attempting to store comm: {}", c.getId());
    // try a call to store
    this.storer.store(c);
    return null;
  }
}
