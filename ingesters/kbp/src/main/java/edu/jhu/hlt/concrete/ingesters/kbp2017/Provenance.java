package edu.jhu.hlt.concrete.ingesters.kbp2017;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Provenance.Builder.class)
public abstract class Provenance {
  public abstract String getDocumentID();
  public abstract TextSpan getTextSpan();

  static class Builder extends Provenance_Builder {
    Builder() {

    }
  }
}
