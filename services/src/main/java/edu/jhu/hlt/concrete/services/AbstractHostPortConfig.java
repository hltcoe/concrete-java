package edu.jhu.hlt.concrete.services;

import com.typesafe.config.Config;

public abstract class AbstractHostPortConfig {

  protected final String host;
  protected final int port;

  protected AbstractHostPortConfig(Config cfg) {
    this.host = cfg.getString("host");
    this.port = cfg.getInt("port");
  }

  public final String getHost() {
    return this.host;
  }

  public final int getPort() {
    return this.port;
  }
}
