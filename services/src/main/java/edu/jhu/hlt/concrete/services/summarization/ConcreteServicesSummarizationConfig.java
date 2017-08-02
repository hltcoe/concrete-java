package edu.jhu.hlt.concrete.services.summarization;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.services.AbstractHostPortConfig;
import edu.jhu.hlt.concrete.services.ConcreteServicesConfig;

public class ConcreteServicesSummarizationConfig extends AbstractHostPortConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteServicesSummarizationConfig.class);

  public static final String CONFIG_STRING = "summarization";

  private final Config cfg;

  public ConcreteServicesSummarizationConfig() {
    this(ConfigFactory.load());
  }

  public ConcreteServicesSummarizationConfig(ConcreteServicesConfig cfg) {
    super(cfg.get().getConfig(CONFIG_STRING));
    Config store = cfg.get().getConfig(CONFIG_STRING);
    store.checkValid(ConfigFactory.defaultReference(), CONFIG_STRING);
    this.cfg = store;
    LOGGER.debug("Running with config: {}", this.cfg.toString());
  }

  public ConcreteServicesSummarizationConfig(Config cfg) {
    this(new ConcreteServicesConfig(cfg));
  }

  public SummarizationTool getSummarizationTool() throws TTransportException {
    return new SummarizationTool(this.getHost(), this.getPort());
  }
}
