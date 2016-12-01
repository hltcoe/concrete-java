/*
 *
 */
package edu.jhu.hlt.concrete.services.search;

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

import edu.jhu.hlt.concrete.search.SearchService;
import edu.jhu.hlt.concrete.search.SearchService.Iface;

/**
 * Class that takes in an implementation of {@link SearchService.Iface} and
 * conveniently wraps it in the appropriate transport and protocol (in this
 * case, {@link TCompactProtocol} and {@link TFramedTransport}).
 * <br><br>
 * Additionally implements {@link Runnable} to easily launch in a thread.
 */
public class SearchServiceWrapper implements AutoCloseable, Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceWrapper.class);

  private final TNonblockingServerTransport serverXport;
  private final TServer server;
  private final TNonblockingServer.Args servArgs;

  /**
   * @param impl
   * @param port
   * @throws TException
   */
  public SearchServiceWrapper(SearchService.Iface impl, int port) throws TException {
    this.serverXport = new TNonblockingServerSocket(port);
    final TNonblockingServer.Args args = new TNonblockingServer.Args(this.serverXport);
    args.protocolFactory(new TCompactProtocol.Factory());
    final TFramedTransport.Factory transFactory = new TFramedTransport.Factory(Integer.MAX_VALUE);
    args.transportFactory(transFactory);
    SearchService.Processor<Iface> proc = new SearchService.Processor<>(impl);
    args.processorFactory(new TProcessorFactory(proc));
    args.maxReadBufferBytes = Long.MAX_VALUE;

    this.servArgs = args;
    this.server = new TNonblockingServer(this.servArgs);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    this.server.serve();
    LOGGER.debug("Server is serving.");
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() {
    LOGGER.debug("Preparing to stop.");
    this.server.stop();
    LOGGER.debug("Preparing to close.");
    this.serverXport.close();
  }
}
