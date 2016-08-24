package edu.jhu.hlt.concrete.miscommunication;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.TextSpan;

@FreeBuilder
public abstract class MiscTextSpan {

  public abstract int getStart();
  public abstract int getEnd();
  public abstract NonEmptyString getText();

  public static MiscTextSpan create(TextSpan ts, NonEmptyString docText) {
    return create(ts.getStart(), ts.getEnding(), docText);
  }

  public static MiscTextSpan create(int start, int end, NonEmptyString docText) {
    final int docTextLen = docText.getContent().length();
    if (end > docTextLen)
      throw new IllegalArgumentException("End of span cannot be greater than length of document text.");
    return new Builder()
        .setStart(start)
        .setEnd(end)
        .setText(NonEmptyString.create(docText.getContent().substring(start, end)))
        .build();
  }

  MiscTextSpan() {
  }

  static class Builder extends MiscTextSpan_Builder {
    Builder() {
    }

    @Override
    public Builder setStart(int start) {
      if (start < 0)
        throw new IllegalArgumentException("Start cannot be < 0.");
      return super.setStart(start);
    }
  }
}
