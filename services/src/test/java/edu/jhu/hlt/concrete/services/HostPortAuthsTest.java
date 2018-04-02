package edu.jhu.hlt.concrete.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

public class HostPortAuthsTest {

  @Test
  public void empty() {
    assertEquals(0, HostPortAuths.parse("").size());
  }

  @Test
  public void validNoAuth() {
    List<HostPortAuths> results = HostPortAuths.parse("foo:41414");
    assertEquals(1, results.size());
    HostPortAuths first = results.get(0);
    assertEquals("foo", first.host());
    assertEquals(41414, first.port());
    assertFalse(first.auths().isPresent());
  }

  @Test
  public void validAuth() {
    List<HostPortAuths> results = HostPortAuths.parse("foo:41414:qux");
    assertEquals(1, results.size());
    HostPortAuths first = results.get(0);
    assertEquals("foo", first.host());
    assertEquals(41414, first.port());
    assertEquals("qux", first.auths().get());
  }

  @Test(expected=IllegalArgumentException.class)
  public void badPort() {
    List<HostPortAuths> results = HostPortAuths.parse("foo:41414441:qux");
    assertEquals(1, results.size());
    HostPortAuths first = results.get(0);
    assertEquals("foo", first.host());
    assertEquals(41414, first.port());
    assertEquals("qux", first.auths().get());
  }

  @Test(expected=IllegalArgumentException.class)
  public void badPortString() {
    List<HostPortAuths> results = HostPortAuths.parse("foo:hello:qux");
    assertEquals(1, results.size());
    HostPortAuths first = results.get(0);
    assertEquals("foo", first.host());
    assertEquals(41414, first.port());
    assertEquals("qux", first.auths().get());
  }

  @Test
  public void multi() {
    List<HostPortAuths> results =
        HostPortAuths.parse("foo:41414:qux,foo:31313,bar.com:21211:world");
    assertEquals(3, results.size());
    {
      HostPortAuths hp = results.get(0);
      assertEquals("foo", hp.host());
      assertEquals(41414, hp.port());
      assertEquals("qux", hp.auths().get());
    }
    {
      HostPortAuths hp = results.get(1);
      assertEquals("foo", hp.host());
      assertEquals(31313, hp.port());
      assertFalse(hp.auths().isPresent());
    }
    {
      HostPortAuths hp = results.get(2);
      assertEquals("bar.com", hp.host());
      assertEquals(21211, hp.port());
      assertEquals("world", hp.auths().get());
    }
  }
}
