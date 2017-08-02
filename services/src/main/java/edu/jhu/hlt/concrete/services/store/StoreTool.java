package edu.jhu.hlt.concrete.services.store;

import java.util.Optional;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.services.AbstractAuthBasedThriftServiceClient;

/**
 * Simple client implementation of {@link StoreCommunicationService}.
 * Contains a single method, {@link #store(Communication)}, that merely wraps
 * {@link StoreCommunicationService.Iface#store(Communication)}.
 *
 * @see AbstractAuthBasedThriftServiceClient
 */
public class StoreTool extends AbstractAuthBasedThriftServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(StoreTool.class);

  private final StoreCommunicationService.Client client;

  public StoreTool() throws TTransportException {
    this(ConfigFactory.load());
  }

  public StoreTool(Config cfg) throws TTransportException {
    this(new ConcreteServicesStoreConfig(cfg));
  }

  public StoreTool(ConcreteServicesStoreConfig cfg) throws TTransportException {
    this(cfg.getHost(), cfg.getPort(), cfg.getAuths());
  }

  public StoreTool(String host, int port, String auths) throws TTransportException {
    this(host, port, Optional.ofNullable(auths));
  }

  public StoreTool(String host, int port, Optional<String> auths) throws TTransportException {
    super(host, port, auths);
    LOGGER.debug("Running with host: {}, port: {}, auths: {}", host, port, auths);
    this.client = new StoreCommunicationService.Client(protocol);
  }

  /**
   * Send a {@link Communication} to the target server.
   *
   * @param c a {@link Communication} to send across the wire
   * @throws TException on thrift error
   *
   * @see Sender.Iface#store(Communication)
   */
  public void store(Communication c) throws TException {
    this.client.store(c);
  }
}
