/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package concrete.server;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import edu.jhu.hlt.concrete.server.ConcreteServer;

/**
 *
 */
public class AbstractServiceTest {

  public static final int LISTEN_PORT = 44551;

  protected ConcreteServer srv;
  protected Thread serviceThread;
  protected TTransport xport;
  protected TProtocol protocol;

  /**
   *
   */
  protected AbstractServiceTest() {

  }

  protected void initializeClientFields() throws TTransportException {
    final TSocket sock = new TSocket("localhost", LISTEN_PORT);
    // arbitrary
    sock.setTimeout(100 * 1000);
    this.xport = new TFramedTransport(sock);
    this.xport.open();
    // no idea if below is best - stolen from accumulo
    // when my old method didn't work.
    final TProtocol protocol = new TCompactProtocol.Factory().getProtocol(this.xport);
    this.protocol = protocol;
  }
}
