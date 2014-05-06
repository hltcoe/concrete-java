/**
 * 
 */
package concrete.ontology;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;



/**
 * @author max
 *
 */
public class ConcreteOntology {
  
  public static final String ONTOLOGY_FILE_NAME = "concrete.owl";
  
  private static final Logger logger = LoggerFactory.getLogger(ConcreteOntology.class);
  
  protected final OntModel model;
  protected final String defaultNamespace;
  
  protected final Set<String> commTypes;
  protected final Set<String> sectionTypes;
  
  /**
   * 
   */
  public ConcreteOntology() {
    this(ONTOLOGY_FILE_NAME);
  }
  
  public ConcreteOntology(String fileName) {
    this(ClassLoader.getSystemResourceAsStream(fileName));
  }
  
  public ConcreteOntology(InputStream is) {
    this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
    model.read(is, null);
    
    this.defaultNamespace = this.model.getNsPrefixURI("");
    this.commTypes = new HashSet<String>();
    this.sectionTypes = new HashSet<>();
  }
  
  public final Set<String> getValidCommunicationTypes() {
    if (this.commTypes.size() == 0)
      this.commTypes.addAll(this.loadViaOntology("Communication"));
      
    return new HashSet<>(this.commTypes);
  }
  
  public final Set<String> getValidSectionTypes() {
    if (this.sectionTypes.size() == 0)
      this.sectionTypes.addAll(this.loadViaOntology("Section"));
    
    return new HashSet<>(this.sectionTypes);
  }
  
  private Set<String> loadViaOntology(String ontString) {
    Set<String> toLoadInto = new HashSet<>();  
    OntClass oc = this.model.getOntClass(this.toURI(ontString));
      ExtendedIterator<OntClass> ei = oc.listSubClasses(false);
      while (ei.hasNext()) {
        OntClass occ = ei.next();
        toLoadInto.add(occ.getLocalName());
      }
    
    return toLoadInto;
  }

  public static final String toURI(QName qn) {
    return qn.getNamespaceURI() + qn.getLocalPart();
  }
  
  public final String toURI(String local) {
    return toURI(this.toQName(local));
  }

  public final String getDefaultNamespace() {
    return this.defaultNamespace;
  }
  
  public final QName toQName(String local) {
    return new QName(this.defaultNamespace, local);
  }
  
  public static void main (String... args) {
    ConcreteOntology co = new ConcreteOntology();
    logger.info("Communication Types:");
    for (String s : co.getValidCommunicationTypes())
      logger.info(s);
    logger.info("Section Types:");
    for (String s : co.getValidSectionTypes())
      logger.info(s);
  }
}
