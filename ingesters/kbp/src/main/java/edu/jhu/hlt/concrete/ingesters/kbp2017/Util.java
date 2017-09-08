package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.List;

import com.google.common.collect.ImmutableList;

class Util {

  static final TextSpan parseHyphenatedSpan(String hyp) {
    String[] spl = hyp.split("-");
    return TextSpan.create(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]));
  }

  static final List<Provenance> splitSemicolonLine(String provCol) {
    ImmutableList.Builder<Provenance> ilb = ImmutableList.builder();
    String[] bySemi = provCol.split(";");
    for (String s : bySemi) {
      if (s.equals("NIL"))
        break;

      ilb.add(splitSingleColonLine(s));
    }
    return ilb.build();
  }

  static final Provenance splitSingleColonLine(String line) {
    String[] spl = line.split(":");
    if (spl.length != 2) {
      throw new IllegalArgumentException("not a single colon line: " + line);
    }

    TextSpan ts = Util.parseHyphenatedSpan(spl[1]);
    return new Provenance.Builder()
        .setTextSpan(ts)
        .setDocumentID(spl[0])
        .build();
  }

  static boolean isProvenanceLine(String[] line) {
    return line.length > 3 && line[3].indexOf('_') > 0;
  }
}
