package edu.jhu.hlt.concrete.services.search;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.search.SearchService;
import edu.jhu.hlt.concrete.services.AbstractHostPortOptionalAuthsConfig;
import edu.jhu.hlt.concrete.services.ConcreteServicesConfig;

public class ConcreteServicesSearchConfig extends AbstractHostPortOptionalAuthsConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteServicesSearchConfig.class);

  public static final String SEARCH_CONFIG_STRING = "search";

  private final Config cfg;

  public ConcreteServicesSearchConfig() {
    this(ConfigFactory.load());
  }

  public ConcreteServicesSearchConfig(ConcreteServicesConfig cfg) {
    super(cfg.get().getConfig(SEARCH_CONFIG_STRING));
    Config search = cfg.get().getConfig(SEARCH_CONFIG_STRING);
    search.checkValid(ConfigFactory.defaultReference(), SEARCH_CONFIG_STRING);
    this.cfg = search;
    LOGGER.debug("Running with config: {}", this.cfg.toString());
  }

  public ConcreteServicesSearchConfig(Config cfg) {
    this(new ConcreteServicesConfig(cfg));
  }

  public SearchServiceWrapper createWrapper(SearchService.Iface impl) throws TException {
    return new SearchServiceWrapper(impl, this.getPort());
  }
}
