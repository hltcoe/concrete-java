package edu.jhu.hlt.concrete.redis;

import java.net.URI;
import java.net.URISyntaxException;

import redis.clients.jedis.JedisPool;

final class RedisWrapper {
  private final String host;
  private final int port;

  RedisWrapper(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public String getURIString() {
    return "http://" + this.host + ":" + this.port;
  }

  public URI getURI() throws URISyntaxException {
    return new URI(this.getURIString());
  }

  public JedisPool getJedisPool() throws URISyntaxException {
    return new JedisPool(this.getURI(), 60 * 1000);
  }
}