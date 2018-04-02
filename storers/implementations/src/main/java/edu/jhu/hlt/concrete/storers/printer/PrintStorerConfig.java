/**
 *
 */
package edu.jhu.hlt.concrete.storers.printer;

import java.util.Optional;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.services.store.ConcreteServicesStoreConfig;

/**
 *
 */
public class PrintStorerConfig {
  private final Config cfg;
  private final ConcreteServicesStoreConfig inputCfg;

  public PrintStorerConfig(Config cfg) {
    // store the root config
    this.cfg = cfg;

    // expecting a "printer" key here
    this.cfg.checkValid(ConfigFactory.defaultReference(), "printer");

    // should be a store config
    this.inputCfg = new ConcreteServicesStoreConfig(this.cfg, "printer");
  }

  public PrintStorerConfig() {
    this(ConfigFactory.load());
  }

  public int getPort() {
    return this.inputCfg.getPort();
  }

  public String getHost() {
    return this.inputCfg.getHost();
  }

  public Optional<String> getAuths() {
    return this.inputCfg.getAuths();
  }
}
