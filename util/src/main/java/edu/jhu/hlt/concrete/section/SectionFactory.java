/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.section;

import java.util.stream.Stream;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

/**
 * Utility class for working with Concrete {@link Section} objects.
 */
public class SectionFactory {

  private final AnalyticUUIDGenerator gen;

  public SectionFactory(final AnalyticUUIDGenerator gen) {
    this.gen = gen;
  }

  public SectionFactory(final Communication comm) {
    this.gen = new AnalyticUUIDGeneratorFactory(comm).create();
  }

  /**
   *
   * @return a {@link Section} with a {@link UUID}
   */
  public final Section create() {
    return new Section()
      .setUuid(this.gen.next());
  }

  public final Section fromTextSpan(TextSpan ts, String sectionKind) throws ConcreteException {
    return create()
        .setKind(sectionKind)
        .setTextSpan(ts);
  }

  public final Stream<Section> fromTextSpanStream(Stream<TextSpanKindTuple> tuples) {
    return tuples
        .sequential()
        .map(t -> create().setKind(t.getKind()).setTextSpan(t.getTs()));
  }
}
