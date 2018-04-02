/**
 *
 */
package edu.jhu.hlt.concrete.storers.multistorer;

import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.services.HostPortAuths;
import edu.jhu.hlt.concrete.services.store.ConcreteServicesStoreConfig;

/**
 *
 */
public class MultiStorerConfig {
  private final Config cfg;
  private final ConcreteServicesStoreConfig inputCfg;
  private final ConcreteServicesStoreConfig outputCfg;
  private final List<HostPortAuths> alternatives;

  public MultiStorerConfig(Config cfg) {
    // store the root config
    this.cfg = cfg;

    // expecting a "multistorer" key here
    this.cfg.checkValid(ConfigFactory.defaultReference(), "multistorer");

    // retrieve the input config - should be a store config
    Config input = this.cfg.getConfig("input");
    this.inputCfg = new ConcreteServicesStoreConfig(input);

    // retrieve the output config - should be a store config
    Config output = this.cfg.getConfig("output");
    this.outputCfg = new ConcreteServicesStoreConfig(output);

    // retrieve any other configs - should turn into store-like things
    String alternatives = this.cfg.getString("others");
    this.alternatives = HostPortAuths.parse(alternatives);
  }

  public MultiStorerConfig() {
    this(ConfigFactory.load());
  }
}
