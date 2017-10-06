package edu.jhu.hlt.concrete.ingesters.kbp2017;

import java.util.List;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = Relation.Builder.class)
public abstract class Relation {
  public abstract String getEvent();
  public abstract String getTarget();
  public abstract double getConfidence();
  public abstract List<Provenance> getProvenances();

  /**
   * @param docID the target document ID
   * @return a {@link List} of {@link Provenance}s that appear within the given document
   */
  public final List<Provenance> withinDocumentProvenances(String docID) {
    return this.getProvenances().stream()
        .filter(p -> p.withinDocument(docID))
        .collect(Collectors.toList());
  }

  public final boolean containsAtLeastOneProvenanceFromDocument(String docID) {
    return !this.withinDocumentProvenances(docID).isEmpty();
  }

  static class Builder extends Relation_Builder {
    Builder() {

    }
  }
}
