/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.section;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * A class that exposes a method for converting {@link String} objects,
 * representing contents of a {@link Communication} for example, into a single
 * Concrete {@link Section}.
 */
public class SingleSectionSegmenter {

  private SingleSectionSegmenter() {

  }

  /**
   * Create a single {@link Section} based on some text, with the kind set to
   * sectionKind.
   *
   * @param text the {@link String} upon which to create the Section object
   * @param sectionKind the kind of the Section
   * @return a {@link Section} with one {@link TextSpan}.
   */
  public static final Section createSingleSection (String text, String sectionKind) {
    TextSpan ts = new TextSpan(0, text.length());

    Section s = new Section();
    s.setUuid(UUIDFactory.newUUID());
    s.setKind(sectionKind);
    s.setTextSpan(ts);

    return s;
  }
}
