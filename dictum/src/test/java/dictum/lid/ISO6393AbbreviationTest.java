/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package dictum.lid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.jhu.hlt.concrete.dictum.lid.ISO6393Abbreviation;
import edu.jhu.hlt.concrete.dictum.lid.InvalidISO6393AbbreviationException;

public class ISO6393AbbreviationTest {

  @Test(expected=InvalidISO6393AbbreviationException.class)
  public void wrongLengthAbbreviation() throws Exception {
    ISO6393Abbreviation.fromAbbreviation("fooqux");
  }

  @Test(expected=InvalidISO6393AbbreviationException.class)
  public void badAbbreviation() throws Exception {
    ISO6393Abbreviation.fromAbbreviation("vzv");
  }

  @Test
  public void legit() throws Exception {
    assertEquals("eng",
        ISO6393Abbreviation.fromAbbreviation("eng").getAbbreviation());
  }
}
