/**
 *
 */
package edu.jhu.hlt.concrete.uuid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * @author max
 *
 */
public class AnalyticUUIDGeneratorFactoryTest {

  public static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

  @Test
  public void testZeroPaddedHex() {
    String zph = AnalyticUUIDGenerator.zeroPaddedHex(9, 4);
    assertEquals("0009", zph);
    assertEquals("000f", AnalyticUUIDGenerator.zeroPaddedHex(15, 4).toLowerCase());
    assertEquals("0caf", AnalyticUUIDGenerator.zeroPaddedHex(15 + 10*16 + 12*256, 4).toLowerCase());
  }

  @Test
  public void testGeneratorScratch() {
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory();
    AnalyticUUIDGenerator gen = f.create();
    UUID next = gen.next();
    assertTrue(UUID_PATTERN.matcher(next.getUuidString()).matches());
  }

  @Test
  public void testGeneratorPrefix() {
    int n = 1000;
    String u = "7575a428-aaf7-4c2e-929e-1e2a0ab59e16";
    UUID uid = new UUID(u);
    Communication c = new Communication();
    c.setUuid(uid);
    AnalyticUUIDGeneratorFactory f = new AnalyticUUIDGeneratorFactory(c);
    assertEquals(u, f.getUuidString());
    AnalyticUUIDGenerator gen = f.create();
    UUID next = gen.next();
    assertTrue(UUID_PATTERN.matcher(next.getUuidString()).matches());
    for (int i = 0; i < n; i++) {
      AnalyticUUIDGenerator lgen = f.create();
      assertTrue(lgen.next().getUuidString().startsWith(u.substring(0, 13)));
    }
  }

  @Test
  public void testHexUnifLen() {
    String res = AnalyticUUIDGeneratorFactory.generateHexUnif(21);
    assertEquals(21, res.length());
    Set<Character> charSet = new HashSet<>();
    for (int i = 0; i < res.length(); i++)
      charSet.add(res.charAt(i));
    assertTrue(charSet.size() > 1);
  }

  @Test
  public void testHexUnifSpread() {
    int n = 1000;
    int m = 32;
    Set<String> outs = new HashSet<>(n);
    for (int i = 0; i < n; i++)
      outs.add(AnalyticUUIDGeneratorFactory.generateHexUnif(m));
    assertEquals(1000, outs.size());
  }
}
