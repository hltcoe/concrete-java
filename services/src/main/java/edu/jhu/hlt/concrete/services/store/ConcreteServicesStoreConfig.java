package edu.jhu.hlt.concrete.services.store;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.services.AbstractHostPortOptionalAuthsConfig;
import edu.jhu.hlt.concrete.services.ConcreteServicesConfig;

public class ConcreteServicesStoreConfig extends AbstractHostPortOptionalAuthsConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteServicesStoreConfig.class);

  public static final String CONFIG_STRING = "store";

  private final Config cfg;

  public ConcreteServicesStoreConfig() {
    this(ConfigFactory.load());
  }

  public ConcreteServicesStoreConfig(ConcreteServicesConfig cfg) {
    super(cfg.get().getConfig(CONFIG_STRING));
    Config store = cfg.get().getConfig(CONFIG_STRING);
    store.checkValid(ConfigFactory.defaultReference(), CONFIG_STRING);
    this.cfg = store;
    LOGGER.debug("Running with config: {}", this.cfg.toString());
  }

  public ConcreteServicesStoreConfig(Config cfg) {
    this(new ConcreteServicesConfig(cfg));
  }

  public StoreServiceWrapper createWrapper(StoreCommunicationService.Iface impl) throws TException {
    return new StoreServiceWrapper(impl, this.getPort());
  }

  public StoreTool storeTool() throws TTransportException {
    return new StoreTool(this.getHost(), this.getPort(), this.getAuths());
  }
}
