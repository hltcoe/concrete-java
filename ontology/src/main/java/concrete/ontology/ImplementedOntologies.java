/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package concrete.ontology;


/**
 * @author max
 *
 */
public enum ImplementedOntologies {

  CONCRETE_300 {
    @Override
    public String getFileName() {
      return "concrete-3.0.0.owl";
    }
  }, STANFORD_POS_331 {
    @Override
    public String getFileName() {
      return "stanford-pos-tags-3.3.1.owl";
    }
  }, STANFORD_NER_331 {
    @Override
    public String getFileName() {
      return "stanford-ner-tags-3.3.1.owl";
    }
  };

  public abstract String getFileName();
  public final ConcreteOntology getOntology() {
    return new ConcreteOntology(this.getFileName());
  }
}
