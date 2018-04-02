package edu.jhu.hlt.concrete.services;

import java.util.List;
import java.util.Optional;

import org.apache.thrift.transport.TTransportException;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;

import edu.jhu.hlt.concrete.services.store.StoreTool;

/**
 * HostPortAuths stores host, port, and auth info. Useful for interacting
 * with Concrete services that optionally take an auth parameter.
 */
public class HostPortAuths {
  private final HostAndPort hp;
  private final Optional<String> auths;

  public HostPortAuths(String host, int port, Optional<String> auths) {
    HostAndPort hp = HostAndPort.fromParts(host, port);
    this.hp = hp;
    this.auths = auths;
  }

  public HostPortAuths(String host, int port) {
    this(host, port, Optional.empty());
  }

  public String host() {
    return this.hp.getHost();
  }

  public int port() {
    return this.hp.getPort();
  }

  public HostAndPort hostAndPort() {
    return this.hp;
  }

  public Optional<String> auths() {
    return this.auths;
  }

  /**
   * @return a {@link StoreTool} based on this {@link HostPortAuths}
   * @throws TTransportException on failure to connect
   */
  public StoreTool storeTool() throws TTransportException {
    return new StoreTool(this.host(), this.port(), this.auths);
  }

  /**
   * Parse expects a format like:
   * <br><br>
   * <code>
   * "host:port:auths,host2:port2,host3:port3:auths3"
   * </code>
   * <br><br>
   * and attempts to return a list of {@link HostPortAuths} by first splitting
   * by comma, and then by colon.
   *
   * @param input a {@link String} conforming to the above spec
   * @return
   * @throws IllegalArgumentException on invalid input
   */
  public static List<HostPortAuths> parse(String input) {
    if (input == null || input.length() == 0)
      return ImmutableList.of();

    ImmutableList.Builder<HostPortAuths> b = ImmutableList.builder();
    Iterable<String> items = Splitter.on(',').omitEmptyStrings().split(input);
    Splitter subsplitter = Splitter.on(':').omitEmptyStrings();
    for (String i : items) {
      Iterable<String> subspl = subsplitter.split(i);
      ImmutableList<String> extracted = ImmutableList.copyOf(subspl);
      final int len = extracted.size();

      // only process valid sub inputs
      if (len == 2 || len == 3) {
        String host = extracted.get(0);
        int port = Integer.parseInt(extracted.get(1));
        Optional<String> auths = len == 3 ? Optional.of(extracted.get(2)) : Optional.empty();
        b.add(new HostPortAuths(host, port, auths));
      }
    }
    return b.build();
  }
}
