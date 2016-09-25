/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.redis;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Class for reading and accessing configuration values in Scion.
 */
public final class ConcreteRedisConfig {

  private final Config redisCfg;

  public ConcreteRedisConfig() {
    this(ConfigFactory.load());
  }

  public ConcreteRedisConfig(Config cfg) {
    cfg.checkValid(ConfigFactory.defaultReference(), "concrete", "redis");
    this.redisCfg = cfg.getConfig("concrete").getConfig("redis");
  }

  public final ConcreteRedisPullConfig getPullConfig() {
    return new ConcreteRedisPullConfig(this.redisCfg);
  }

  public final ConcreteRedisPushConfig getPushConfig() {
    return new ConcreteRedisPushConfig(this.redisCfg);
  }

  public final int getSleepTime() {
    return this.redisCfg.getInt("sleep.interval");
  }
}
