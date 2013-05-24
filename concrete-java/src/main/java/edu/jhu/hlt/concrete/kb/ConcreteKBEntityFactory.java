/**
 * 
 */
package edu.jhu.hlt.concrete.kb;

import edu.jhu.hlt.concrete.Concrete.UUID;
import edu.jhu.hlt.concrete.Concrete.Vertex;

import edu.jhu.hlt.concrete.Concrete.VertexKindAttribute;
import edu.jhu.hlt.concrete.util.IdUtil;

/**
 * @author max
 *
 */
public class ConcreteKBEntityFactory {

	public ConcreteKBEntityFactory() {
		
	}
	
	public static Vertex generateVertex() {
		UUID id = IdUtil.generateUUID();
		return Vertex.newBuilder().setUuid(id).build();
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
