package edu.jhu.hlt.concrete.miscommunication.tokenized;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TokenIndexComparatorTest {

  @Test
  public void test() {
    JavaTaggedToken tt1 = new JavaTaggedToken.Builder()
        .setTokenIndex(1)
        .build();
    JavaTaggedToken tt2 = new JavaTaggedToken.Builder()
        .setTokenIndex(500)
        .build();
    JavaTaggedToken tt3 = new JavaTaggedToken.Builder()
        .setTokenIndex(20)
        .build();
    List<JavaTaggedToken> ttl = new ArrayList<>();
    ttl.add(tt1);
    ttl.add(tt2);
    ttl.add(tt3);
    Collections.sort(ttl, new TokenIndexComparator());
    assertEquals(tt1, ttl.get(0));
    assertEquals(tt2, ttl.get(2));
  }
}
