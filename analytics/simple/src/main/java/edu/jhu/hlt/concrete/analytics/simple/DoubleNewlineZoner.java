package edu.jhu.hlt.concrete.analytics.simple;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.analytics.base.AnalyticException;
import edu.jhu.hlt.concrete.analytics.base.Zoner;
import edu.jhu.hlt.concrete.miscommunication.comms.TextCommunication;
import edu.jhu.hlt.concrete.section.SectionFactory;
import edu.jhu.hlt.concrete.section.TextSpanKindTuple;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory;
import edu.jhu.hlt.concrete.uuid.AnalyticUUIDGeneratorFactory.AnalyticUUIDGenerator;

public class DoubleNewlineZoner implements Zoner {

  private final String sectionKindLabel;
  private static final String lineSep = System.lineSeparator();
  private static final String doubleLineSep = lineSep + lineSep;

  public DoubleNewlineZoner(String sectionKind) {
    this.sectionKindLabel = sectionKind;
  }

  @Override
  public List<Section> zone(TextCommunication comm) throws AnalyticException {
    String[] split2xNewline = comm.getText().split(doubleLineSep);
    Stream.Builder<TextSpanKindTuple> stream = Stream.builder();
    int charCtr = 0;
    for (String s : split2xNewline) {
      final int len = s.length();
      final int sum = len + charCtr;
      TextSpan ts = new TextSpan(charCtr, sum);
      charCtr = sum + 2;
      stream.add(new TextSpanKindTuple(ts, this.sectionKindLabel));
    }

    AnalyticUUIDGeneratorFactory fact = new AnalyticUUIDGeneratorFactory(comm.getCommunication().getUUID());
    AnalyticUUIDGenerator g = fact.create();
    Stream<Section> sections = new SectionFactory(g).fromTextSpanStream(stream.build());
    return ImmutableList.copyOf(sections.collect(Collectors.toList()));
  }
}
