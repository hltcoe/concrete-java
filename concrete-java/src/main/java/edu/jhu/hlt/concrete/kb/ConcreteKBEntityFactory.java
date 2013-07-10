/**
 * 
 */
package edu.jhu.hlt.concrete.kb;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.Concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Graph.FloatAttribute;
import edu.jhu.hlt.concrete.Graph.StringAttribute;
import edu.jhu.hlt.concrete.Graph.Vertex;
import edu.jhu.hlt.concrete.Graph.VertexKindAttribute;
import edu.jhu.hlt.concrete.util.IdUtil;

/**
 * @author max
 *
 */
public class ConcreteKBEntityFactory {

	private static final AnnotationMetadata tacKBMetadata;
	
	static {
		// This is our ground truth, so 1.0 confidence.
		// Tool name arbitrary.
		tacKBMetadata = AnnotationMetadata.newBuilder()
				.setConfidence(1.0f)
				.setTool("KB Ground Truth")
				.build();
	}
	
	public ConcreteKBEntityFactory() {
		
	}
	
	public static Vertex generateBareVertex() {
		Concrete.UUID id = IdUtil.generateUUID();
		return Vertex.newBuilder().setUuid(id).build();
	}
	
	/**
	 * Example method for generating a Vertex whose type
	 * is a {@link Vertex.Kind.PERSON}.
	 * 
	 * @return
	 */
	public static Vertex generatePersonVertex(String name, String nationality, 
			float age) {
		// Generate a UUID.
	        Concrete.UUID id = IdUtil.generateUUID();
		
		// Generate a Builder object. 
		Vertex.Builder vBuilder = Vertex.newBuilder();
		
		// Set the ID.
		// Since these are singular values, we use "set" (not "add").
		vBuilder.setUuid(id);
		
		// Build the kind (type) object. 
		VertexKindAttribute.Builder vkaBuilder = VertexKindAttribute.newBuilder();
		vkaBuilder.setValue(Vertex.Kind.PERSON);
		// Set up metadata - see above static block for example.
		vkaBuilder.setMetadata(tacKBMetadata);
		
		// Set the kind (type).
		// Since we can have more than one "type" (e.g., various
		// confidences for each one), we use "add".
		vBuilder.addKind(vkaBuilder);
		
		// Set up a name object.
		// This is complicated b/c protocol buffers can't do key-value pairs.
		// We also include a metadata object. Just use the one for the class.
		StringAttribute nameAttr = StringAttribute.newBuilder()
				.setValue(name)
				.setMetadata(tacKBMetadata)
				.build();
		
		// Set the actual name object on the vertex.
		// IF you have >1 name, create many StringAttributes
		// and add them all.
		vBuilder.addName(nameAttr);
		
		// Set up a nationality object.
		// Note similarity to "name" - basically just a key value pair.
		StringAttribute natlAttr = StringAttribute.newBuilder()
				.setValue(nationality)
				.setMetadata(tacKBMetadata)
				.build();
		
		vBuilder.addNationality(natlAttr);
		
		// Set up age object.
		// Note that it is a "FloatAttribute" basically a key-value
		// pair whose key is a float.
		FloatAttribute ageAttr = FloatAttribute.newBuilder()
				.setValue(age)
				.setMetadata(tacKBMetadata)
				.build();
		
		// Assign age to vertex.
		vBuilder.addAge(ageAttr);
		
		// You can also add:
		// communication, communicationGuids, etc.
		// Take a look at the protocol buffer file
		// to see all possibilities (lines ~1540ish).
		
		// When finished, build the object and return.
		// This assembled the data into one big concrete object.
		return vBuilder.build();
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
