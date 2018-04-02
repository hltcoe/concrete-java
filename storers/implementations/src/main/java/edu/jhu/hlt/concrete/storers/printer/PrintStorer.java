package edu.jhu.hlt.concrete.storers.printer;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.storers.DataflowStoreWriter;
import groovyx.gpars.dataflow.DataflowQueue;

public class PrintStorer implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(PrintStorer.class);

  private DataflowStoreWriter commsChan;
  private Thread printThread;
  private AtomicBoolean keepRunning = new AtomicBoolean(true);

  public PrintStorer() {
    // create a 1-to-1 channel
    DataflowQueue<Communication> incomingComms = new DataflowQueue<>();
    this.commsChan = new DataflowStoreWriter(incomingComms);

    Runnable printer = () -> {
      while(keepRunning.get()) {
        try {
          Communication c = incomingComms.getVal();
          LOG.info("UUID: {} | ID: {}", c.getUuid().getUuidString(), c.getId());
        } catch (InterruptedException e) {
          LOG.info("interrupted");
        }
      }
    };

    this.printThread = new Thread(printer, "PrinterThread");
    this.printThread.start();
  }

  public StoreCommunicationService.Iface storeImpl() {
    return this.commsChan;
  }

  @Override
  public void close() throws InterruptedException {
    this.keepRunning.set(false);
    this.printThread.join();
  }
}
