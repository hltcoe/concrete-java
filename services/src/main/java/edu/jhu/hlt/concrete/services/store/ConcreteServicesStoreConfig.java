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

  /**
   * Instantiate a {@link ConcreteServicesStoreConfig} with reasonable defaults.
   * <br/><br/>
   * Users bringing a {@link Config} object (e.g. from a config file that includes
   * a store configuration) should prefer {@link #ConcreteServicesStoreConfig(Config)}.
   *
   * @see #ConcreteServicesStoreConfig(Config)
   */
  public ConcreteServicesStoreConfig() {
    this(ConfigFactory.load());
  }

  /**
   * Instantiate a {@link ConcreteServicesStoreConfig} from a provided {@link Config}.
   *
   * @param cfg a {@link Config} object containing a 'store' key with proper configuration values
   * @see #ConcreteServicesStoreConfig()
   */
  public ConcreteServicesStoreConfig(Config cfg) {
    this(new ConcreteServicesConfig(cfg));
  }

  /**
   * Attempt to load a configuration based off of key 'store' in the config file.
   * <br/><br/>
   * Most clients should prefer {@link #ConcreteServicesStoreConfig(Config)} if
   * bringing a {@link Config} object, or {@link #ConcreteServicesStoreConfig()} if
   * the defaults are tolerable.
   *
   * <br/><br/>
   * See the project's default in
   * <code>src/main/resources/reference.conf</code>
   * for an example.
   *
   *
   * @param cfg a {@link ConcreteServicesStoreConfig} object with a 'store' key
   *      representing a properly configured store implementation.
   * @see #ConcreteServicesStoreConfig()
   * @see #ConcreteServicesStoreConfig(Config)
   */
  public ConcreteServicesStoreConfig(ConcreteServicesConfig cfg) {
    this(cfg, CONFIG_STRING);
  }

  /**
   * Load a store configuration from a non-standard configuration key.
   *
   * Most clients should prefer {@link #ConcreteServicesStoreConfig(Config)} if
   * bringing a {@link Config} object, or {@link #ConcreteServicesStoreConfig()}
   * if the defaults are tolerable.
   *
   * @param cfg
   *          a {@link ConcreteServicesConfig} to use
   * @param key
   *          a {@link String} representing the configuration key (e.g.
   *          'output-store')
   */
  public ConcreteServicesStoreConfig(ConcreteServicesConfig cfg, String key) {
    super(cfg.get().getConfig(key));
    Config store = cfg.get().getConfig(key);
    store.checkValid(ConfigFactory.defaultReference(), key);
    this.cfg = store;
    LOGGER.debug("Running with config: {}", this.cfg.toString());
  }

  public StoreServiceWrapper createWrapper(StoreCommunicationService.Iface impl) throws TException {
    return new StoreServiceWrapper(impl, this.getPort());
  }

  /**
   * @return a {@link StoreTool} configured based on this configuration
   * @throws TTransportException
   *           on failure with connection
   */
  public StoreTool storeTool() throws TTransportException {
    return new StoreTool(this.getHost(), this.getPort(), this.getAuths());
  }
}
