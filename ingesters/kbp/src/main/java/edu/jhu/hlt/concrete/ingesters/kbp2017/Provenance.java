package edu.jhu.hlt.concrete.ingesters.kbp2017;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Provenance.Builder.class)
public abstract class Provenance {
  public abstract String getDocumentID();
  public abstract TextSpan getTextSpan();

  public boolean withinDocument(String id) {
    return this.getDocumentID().equals(id);
  }

  /**
   * TextSpans from TAC KBP knowledge bases are ending INCLUSIVE. This method returns an
   * ending EXCLUSIVE {@link TextSpan}, matching concrete's style.
   *
   * @return a {@link TextSpan} with the ending as EXCLUSIVE
   */
  @JsonIgnore
  public final TextSpan getConcreteStyleTextSpan() {
    return TextSpan.create(this.getTextSpan().getStart(), this.getTextSpan().getEnd() + 1);
  }

  static class Builder extends Provenance_Builder {
    Builder() {

    }
  }
}
