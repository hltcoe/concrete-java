/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.server;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.annotate.AnnotateCommunicationService;
import edu.jhu.hlt.concrete.annotate.AnnotateCommunicationService.Iface;

/**
 * Sample class that provides an easily extendable
 * superclass for implementation of Java-based Concrete
 * Thrift servers.
 */
public class ConcreteServer implements AutoCloseable, Runnable {

  private static final Logger logger = LoggerFactory.getLogger(ConcreteServer.class);

  private TNonblockingServerTransport serverXport;
  private TServer server;
  // private TThreadPoolServer.Args args;
  private TNonblockingServer.Args args;

  /**
   *
   */
  public ConcreteServer(AnnotateCommunicationService.Iface impl, int port) throws ServerException {
    try {
      this.serverXport = new TNonblockingServerSocket(port);
      // TODO: eval HaHs server?
      final TNonblockingServer.Args args = new TNonblockingServer.Args(this.serverXport);
      args.protocolFactory(new TCompactProtocol.Factory());
      // TODO: eval FastFramedTransport?
      final TFramedTransport.Factory transFactory = new TFramedTransport.Factory(Integer.MAX_VALUE);
      args.transportFactory(transFactory);
      // legitimately do not know type bound here - guessing Iface
      AnnotateCommunicationService.Processor<Iface> proc = new AnnotateCommunicationService.Processor<>(impl);
      args.processorFactory(new TProcessorFactory(proc));
      args.maxReadBufferBytes = Long.MAX_VALUE;

      this.args = args;
      // final TNonblockingServer server = new TNonblockingServer(args);
      this.server = new TNonblockingServer(this.args);
    } catch (TTransportException e) {
      throw new ServerException(e);
    }
  }

  public final void start() {
    this.server.serve();
  }

  public final void stop() {
    this.server.stop();
  }

  @Override
  public final void run() {
    this.start();
  }

  @Override
  public final void close() throws Exception {
    logger.debug("AbstractThriftServer closing.");
    if (this.server.isServing())
      this.stop();
    this.serverXport.close();
  }

  public static final void createServer(AnnotateCommunicationService.Iface impl, int port) throws ServerException {
    String implClassString = impl.getClass().toString();
    logger.info("Preparing to start {} on port {}.", implClassString, port);
    try (ConcreteServer srv = new ConcreteServer(impl, port);) {
      srv.start();
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }
}
