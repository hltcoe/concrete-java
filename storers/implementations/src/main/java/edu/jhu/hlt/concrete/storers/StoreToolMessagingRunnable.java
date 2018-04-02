package edu.jhu.hlt.concrete.storers;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.services.store.StoreTool;
import groovyx.gpars.MessagingRunnable;

public class StoreToolMessagingRunnable {

  private static final Logger LOG = LoggerFactory.getLogger(StoreToolMessagingRunnable.class);

  private final StoreTool storer;

  public StoreToolMessagingRunnable(StoreTool storer) {
    this.storer = storer;
  }

  public MessagingRunnable<Communication> toMessagingRunnable () {
    return new MessagingRunnable<Communication>() {
      /**
       *
       */
      private static final long serialVersionUID = -1489977792199820137L;

      @Override
      protected void doRun(Communication arg0) {
        if (arg0.isSetId()) {
          LOG.debug("Processing communication: {}", arg0.getId());
        } else {
          LOG.info("Processing invalid communication (no ID field): {}", arg0.toString());
        }

        try {
          storer.store(arg0);
        } catch (TException e) {
          LOG.warn("Failed to store communication", e);
        }
      }
    };
  }
}
