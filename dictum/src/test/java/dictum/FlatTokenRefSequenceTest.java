/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package dictum;

import java.util.UUID;

import org.junit.Test;

import edu.jhu.hlt.concrete.dictum.FlatTokenGrouping;

/**
 *
 */
public class FlatTokenRefSequenceTest {

  private final UUID uuid = UUID.randomUUID();

  @Test(expected=IllegalArgumentException.class)
  public void dupeIDs() {
    FlatTokenGrouping.Builder b = new FlatTokenGrouping.Builder();
    b.addTokenIndices(1);
    b.addTokenIndices(1);
    b.setTokenizationUUID(uuid);
    b.build();
  }

  @Test(expected=IllegalArgumentException.class)
  public void negatives() {
    FlatTokenGrouping.Builder b = new FlatTokenGrouping.Builder();
    b.addTokenIndices(1);
    b.addTokenIndices(-1);
    b.setTokenizationUUID(uuid);
    b.build();
  }

  @Test(expected=IllegalArgumentException.class)
  public void badAnchor() {
    FlatTokenGrouping.Builder b = new FlatTokenGrouping.Builder();
    b.addTokenIndices(1);
    b.addTokenIndices(4);
    b.setTokenizationUUID(uuid);
    b.setAnchorTokenIndex(3);
    b.build();
  }

  @Test
  public void valid() {
    FlatTokenGrouping.Builder b = new FlatTokenGrouping.Builder();
    b.addTokenIndices(0);
    b.addTokenIndices(1);
    b.addTokenIndices(5);
    b.addTokenIndices(6);
    b.addTokenIndices(60);
    b.setTokenizationUUID(uuid);
    b.setAnchorTokenIndex(5);
    b.build();
  }
}
