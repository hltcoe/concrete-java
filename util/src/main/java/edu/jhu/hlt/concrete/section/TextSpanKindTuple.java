package edu.jhu.hlt.concrete.section;

import edu.jhu.hlt.concrete.TextSpan;

public class TextSpanKindTuple {
  public TextSpan ts;
  public String kind;

  public TextSpanKindTuple(TextSpan ts, String kind) {
    this.ts = ts;
    this.kind = kind;
  }

  /**
   * @return the {@link TextSpan}
   */
  public TextSpan getTs() {
    return ts;
  }

  /**
   * @return the section kind
   */
  public String getKind() {
    return kind;
  }
}