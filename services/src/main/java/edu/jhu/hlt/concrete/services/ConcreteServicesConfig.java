package edu.jhu.hlt.concrete.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConcreteServicesConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConcreteServicesConfig.class);

  public static final String CONCRETE_CONFIG_STRING = "concrete";
  public static final String SERVICES_CONFIG_STRING = "services";

  final Config cfg;

  public ConcreteServicesConfig() {
    this(ConfigFactory.load());
  }

  public ConcreteServicesConfig(Config cfg) {
      LOGGER.debug("Running with config: {}", cfg.toString());
    cfg.checkValid(ConfigFactory.defaultReference(),
        CONCRETE_CONFIG_STRING,
        SERVICES_CONFIG_STRING);
    this.cfg = cfg.getConfig(CONCRETE_CONFIG_STRING)
        .getConfig(SERVICES_CONFIG_STRING);
    LOGGER.debug("Running with concrete services config: {}", this.cfg.toString());
  }

  public Config get() {
    return this.cfg;
  }
}
