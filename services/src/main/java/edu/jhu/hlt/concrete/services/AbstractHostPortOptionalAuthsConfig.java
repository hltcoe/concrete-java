package edu.jhu.hlt.concrete.services;

import java.util.Optional;

import com.typesafe.config.Config;

public abstract class AbstractHostPortOptionalAuthsConfig extends AbstractHostPortConfig {

  protected final Optional<String> auths;

  protected AbstractHostPortOptionalAuthsConfig(Config cfg) {
    super(cfg);
    this.auths = cfg.hasPath("auths") ?
        Optional.of(cfg.getString("auths")) : Optional.empty();
  }

  public final Optional<String> getAuths() {
    return this.auths;
  }
}
