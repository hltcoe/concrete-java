/*
 * 
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
