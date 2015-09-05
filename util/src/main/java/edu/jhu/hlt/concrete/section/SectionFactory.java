/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.section;

import java.util.stream.Stream;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * Utility class for working with Concrete {@link Section} objects.
 */
public class SectionFactory {

  private SectionFactory() {

  }

  /**
   *
   * @return a {@link Section} with a {@link UUID}
   */
  public static final Section create() {
    return new Section()
      .setUuid(UUIDFactory.newUUID());
  }

  public static final Section fromTextSpan(TextSpan ts, String sectionKind) throws ConcreteException {
    return create()
        .setKind(sectionKind)
        .setTextSpan(ts);
  }

  public static final Stream<Section> fromTextSpanStream(Stream<TextSpanKindTuple> tuples) {
    return tuples
        .sequential()
        .map(t -> create().setKind(t.getKind()).setTextSpan(t.getTs()));
  }
}
