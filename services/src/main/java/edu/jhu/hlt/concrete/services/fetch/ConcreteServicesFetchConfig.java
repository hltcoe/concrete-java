package edu.jhu.hlt.concrete.services.fetch;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.services.AbstractHostPortOptionalAuthsConfig;
import edu.jhu.hlt.concrete.services.ConcreteServicesConfig;

public class ConcreteServicesFetchConfig extends AbstractHostPortOptionalAuthsConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteServicesFetchConfig.class);

  public static final String FETCH_CONFIG_STRING = "fetch";

  private final Config cfg;

  public ConcreteServicesFetchConfig() {
    this(ConfigFactory.load());
  }

  public ConcreteServicesFetchConfig(ConcreteServicesConfig cfg) {
    super(cfg.get().getConfig(FETCH_CONFIG_STRING));
    Config search = cfg.get().getConfig(FETCH_CONFIG_STRING);
    search.checkValid(ConfigFactory.defaultReference(), FETCH_CONFIG_STRING);
    this.cfg = search;
    LOGGER.debug("Running with config: {}", this.cfg.toString());
  }

  public ConcreteServicesFetchConfig(Config cfg) {
    this(new ConcreteServicesConfig(cfg));
  }

  public FetchServiceWrapper createWrapper(FetchCommunicationService.Iface impl) throws TException {
    return new FetchServiceWrapper(impl, this.getPort());
  }
}
