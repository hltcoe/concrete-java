/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package dictum;

import static org.quicktheories.quicktheories.QuickTheory.qt;
import static org.quicktheories.quicktheories.generators.SourceDSL.doubles;

import org.junit.Test;

import edu.jhu.hlt.concrete.dictum.primitives.Confidence;

/**
 *
 */
public class ConfidenceTest {

  @Test
  public void validFromZeroToOne() {
    qt().forAll(doubles().fromZeroToOne())
        .checkAssert(cb -> {
          Confidence.Builder b = new Confidence.Builder();
          b.setScore(cb);
          b.build();
        });
  }

  @Test(expected=IllegalArgumentException.class)
  public void lt0() {
    Confidence.Builder b = new Confidence.Builder();
    b.setScore(-0.14d);
    b.build();
  }

  @Test(expected=IllegalArgumentException.class)
  public void nan() {
    Confidence.Builder b = new Confidence.Builder();
    b.setScore(Double.NaN);
    b.build();
  }

  @Test(expected=IllegalArgumentException.class)
  public void gt1() {
    Confidence.Builder b = new Confidence.Builder();
    b.setScore(11.1d);
    b.build();
  }
}
