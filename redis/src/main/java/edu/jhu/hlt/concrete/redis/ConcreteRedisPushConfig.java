/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.redis;

import com.typesafe.config.Config;

/**
 *
 */
public final class ConcreteRedisPushConfig extends AbstractConcreteRedisSubConfig {

  ConcreteRedisPushConfig(Config cfg) {
    super(cfg, "push");
  }

  public final int getLimit() {
    return this.cfg.getInt("limit");
  }

  public final int getPollInterval() {
    return this.cfg.getInt("poll-interval");
  }
}
