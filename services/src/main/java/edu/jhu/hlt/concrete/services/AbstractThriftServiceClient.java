package edu.jhu.hlt.concrete.services;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Abstract class for client tools that consume concrete services.
 */
public abstract class AbstractThriftServiceClient implements AutoCloseable {

  protected final String host;
  protected final int port;

  protected final TTransport transport;
  protected final TCompactProtocol protocol;

  public AbstractThriftServiceClient(AbstractHostPortOptionalAuthsConfig cfg) throws TTransportException {
    this(cfg.getHost(), cfg.getPort());
  }

  public AbstractThriftServiceClient(String host, int port) throws TTransportException {
    this.host = host;
    this.port = port;

    this.transport = new TFramedTransport(new TSocket(host, port), Integer.MAX_VALUE);
    this.protocol = new TCompactProtocol(transport);
    this.transport.open();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() {
    if (this.transport.isOpen())
      this.transport.close();
  }
}
