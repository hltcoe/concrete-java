/*
 * Copyright 2016 JHU HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package dictum.lid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import edu.jhu.hlt.concrete.dictum.lid.ValidISO3Languages;

public class ValidLanguagesTest {

  @Test
  public void testIsValidISO3Abbreviation() {
    assertTrue(ValidISO3Languages.isValidISO3Abbreviation("bom"));
    assertFalse(ValidISO3Languages.isValidISO3Abbreviation("xx1"));
    assertFalse(ValidISO3Languages.isValidISO3Abbreviation(""));
    assertFalse(ValidISO3Languages.isValidISO3Abbreviation("sadf31515"));
    assertTrue(ValidISO3Languages.isValidISO3Abbreviation("eng"));
  }

  @Test
  public void testGetName() {
    Optional<String> res = ValidISO3Languages.getName("bop");
    assertTrue(res.isPresent());
    assertEquals("Bonkiman", res.get());
    assertFalse(ValidISO3Languages.getName("xxx").isPresent());
  }
}
