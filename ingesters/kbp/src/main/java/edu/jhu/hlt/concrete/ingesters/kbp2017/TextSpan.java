package edu.jhu.hlt.concrete.ingesters.kbp2017;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = TextSpan.Builder.class)
public abstract class TextSpan {

  public abstract int getStart();
  public abstract int getEnd();

  public static TextSpan create(edu.jhu.hlt.concrete.TextSpan ts) {
    return create(ts.getStart(), ts.getEnding());
  }

  public final edu.jhu.hlt.concrete.TextSpan toConcrete() {
    return new edu.jhu.hlt.concrete.TextSpan(this.getStart(), this.getEnd());
  }

  public static TextSpan create(int start, int end) {
    return new Builder()
        .setStart(start)
        .setEnd(end)
        .build();
  }

  public boolean within(int offset) {
    return offset >= this.getStart() && offset < this.getEnd();
  }

  public boolean overlaps(TextSpan other) {
    return other.getStart() >= this.getStart()
        && other.getEnd() <= this.getEnd();
  }

  public static class Builder extends TextSpan_Builder {
    public Builder() {

    }
  }
}
