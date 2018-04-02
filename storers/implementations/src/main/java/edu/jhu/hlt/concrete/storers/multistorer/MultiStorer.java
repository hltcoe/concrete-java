package edu.jhu.hlt.concrete.storers.multistorer;

import java.util.List;

import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.services.store.StoreTool;
import edu.jhu.hlt.concrete.storers.DataflowStoreWriter;
import edu.jhu.hlt.concrete.storers.StoreToolMessagingRunnable;
import groovyx.gpars.MessagingRunnable;
import groovyx.gpars.dataflow.DataflowBroadcast;
import groovyx.gpars.dataflow.DataflowReadChannel;

/**
 *
 *
 */
public class MultiStorer {
  private DataflowStoreWriter commsChan;
  private List<StoreToolMessagingRunnable> runners;

  private MultiStorer(Iterable<StoreTool> tools) {
    // create a 1-to-many channel (1 input seen by all observers)
    DataflowBroadcast<Communication> incomingComms = new DataflowBroadcast<>();
    this.commsChan = new DataflowStoreWriter(incomingComms);

    ImmutableList.Builder<StoreToolMessagingRunnable> runners = ImmutableList.builder();
    for (StoreTool st : tools) {
      runners.add(new StoreToolMessagingRunnable(st));
    }
    this.runners = runners.build();
    for (StoreToolMessagingRunnable r : this.runners) {
      // create a pub-sub subscriber
      DataflowReadChannel<Communication> commsFromService = incomingComms.createReadChannel();
      // get the wrapper object
      MessagingRunnable<Communication> wrapper = r.toMessagingRunnable();
      // bind the two together
      commsFromService.wheneverBound(wrapper);
    }
  }

  public MultiStorer(StoreTool first, StoreTool... others) {
    ImmutableList.Builder<StoreTool> runners = ImmutableList.builder();
    runners.add(first);
    if (others != null) {
      runners.add(others);
    }
    new MultiStorer(runners.build());
  }
}
