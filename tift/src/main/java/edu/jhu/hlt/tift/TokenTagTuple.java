/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.tift;

import java.util.Optional;

/**
 * 2-tuple that contains a token and an {@link Optional} tag.
 */
public class TokenTagTuple {

  private final String token;
  private final Optional<String> tag;

  /**
   *
   */
  public TokenTagTuple(String token) {
    this(token, Optional.empty());
  }

  public TokenTagTuple(String token, Optional<String> tag) {
    this.token = token;
    this.tag = tag;
  }

  public TokenTagTuple(String token, String tag) {
    this.token = token;
    this.tag = Optional.ofNullable(tag);
  }

  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * @return the tag
   */
  public Optional<String> getTag() {
    return tag;
  }
}
