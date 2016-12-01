/*
 *
 */
package edu.jhu.hlt.concrete.services.fetch;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.FetchCommunicationService.Iface;

/**
 *
 */
public class FetchServiceWrapper implements AutoCloseable, Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchServiceWrapper.class);

  private final TNonblockingServerTransport serverXport;
  private final TServer server;
  private final TNonblockingServer.Args servArgs;

  public FetchServiceWrapper(FetchCommunicationService.Iface impl, int port) throws TException {
    this.serverXport = new TNonblockingServerSocket(port);
    final TNonblockingServer.Args args = new TNonblockingServer.Args(this.serverXport);
    args.protocolFactory(new TCompactProtocol.Factory());
    final TFramedTransport.Factory transFactory = new TFramedTransport.Factory(Integer.MAX_VALUE);
    args.transportFactory(transFactory);
    FetchCommunicationService.Processor<Iface> proc = new FetchCommunicationService.Processor<>(impl);
    args.processorFactory(new TProcessorFactory(proc));
    args.maxReadBufferBytes = Long.MAX_VALUE;

    this.servArgs = args;
    this.server = new TNonblockingServer(this.servArgs);
  }

  @Override
  public void run() {
    this.server.serve();
    LOGGER.debug("Server is serving.");
  }

  @Override
  public void close() {
    LOGGER.debug("Preparing to stop.");
    this.server.stop();
    LOGGER.debug("Preparing to close.");
    this.serverXport.close();
  }
}
