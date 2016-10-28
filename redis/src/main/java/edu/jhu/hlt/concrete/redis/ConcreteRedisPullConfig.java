/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.redis;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 */
public final class ConcreteRedisPullConfig extends AbstractConcreteRedisSubConfig {

  public ConcreteRedisPullConfig(Config cfg) {
    super(cfg, "pull");
  }

  public ConcreteRedisPullConfig() {
    this(ConfigFactory.load());
  }
}
