package edu.jhu.hlt.concrete.miscommunication.tokenized;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.TaggedToken;

@FreeBuilder
public abstract class JavaTaggedToken {
  public abstract Optional<Integer> getTokenIndex();
  public abstract Optional<String> getTag();
  public abstract Optional<Double> getConfidence();
  public abstract List<String> getTagList();
  public abstract List<Double> getConfidenceList();

  public TaggedToken concrete() {
    TaggedToken tt = new TaggedToken();
    this.getTokenIndex().ifPresent(tt::setTokenIndex);
    this.getTag().ifPresent(tt::setTag);
    this.getConfidence().ifPresent(tt::setConfidence);
    if (!this.getTagList().isEmpty())
      tt.setTagList(this.getTagList());
    if (!this.getConfidenceList().isEmpty())
      tt.setConfidenceList(this.getConfidenceList());
    return tt;
  }

  public static class Builder extends JavaTaggedToken_Builder {
    public Builder() {

    }
  }
}
