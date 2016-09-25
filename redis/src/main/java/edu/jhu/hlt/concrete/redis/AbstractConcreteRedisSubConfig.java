/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.redis;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import redis.clients.jedis.JedisPool;

/**
 * Abstract class for reading and accessing Redis configuration values.
 */
abstract class AbstractConcreteRedisSubConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConcreteRedisSubConfig.class);

  protected final Config cfg;
  private final RedisWrapper wrapper;

  protected AbstractConcreteRedisSubConfig(Config cfg, String key) {
    cfg.checkValid(ConfigFactory.defaultReference(), key);
    this.cfg = cfg.getConfig(key);
    LOGGER.debug("Running with config: {}", this.cfg.toString());
    this.wrapper = new RedisWrapper(this.getHost(), this.getPort());
  }

  public final String getHost() {
    return this.cfg.getString("host");
  }

  public final int getPort() {
    return this.cfg.getInt("port");
  }

  public final String getURIString() {
    return this.wrapper.getURIString();
  }

  public final URI getURI() throws URISyntaxException {
    return this.wrapper.getURI();
  }

  public final JedisPool getJedisPool() throws URISyntaxException {
    return this.wrapper.getJedisPool();
  }

  public final String getKey() {
    return this.cfg.getString("key");
  }

  public final String getContainer() {
    return this.cfg.getString("container");
  }
}
