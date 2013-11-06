/**
 * 
 */
package edu.jhu.hlt.concrete.index;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;

import edu.jhu.hlt.concrete.Concrete;
import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.CommunicationGUID;
import edu.jhu.hlt.concrete.Concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.ConcreteException;
import edu.jhu.hlt.concrete.index.ProtoIndex.ModificationTarget;
import edu.jhu.hlt.concrete.util.ProtoFactory;
import edu.jhu.hlt.tift.ConcreteSectionSegmentation;
import edu.jhu.hlt.tift.Tokenizer;

/**
 * @author max
 * 
 */
public class ProtoIndexTest {

  ProtoIndex pi;
  ProtoFactory pf = new ProtoFactory();
  CommunicationGUID guidOne = pf.generateMockCommGuid();
  Communication commOne = Communication.newBuilder(ProtoFactory.generateCommunication(guidOne)).setText("Sample test text for testing").build();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    this.pi = new ProtoIndex(commOne);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testAddSectionSegmentation() throws InvalidProtocolBufferException, ConcreteException {
    FieldDescriptor ssField = Concrete.Communication.getDescriptor().findFieldByName("section_segmentation");
    SectionSegmentation ssToAppend = ConcreteSectionSegmentation.generateSectionSegmentation(Tokenizer.TWITTER, commOne.getText());
    this.pi.addField(this.pi.getRoot(), ssField, ssToAppend);

    Map<ModificationTarget, byte[]> modMap = this.pi.getUnsavedModifications();
    for (Entry<ModificationTarget, byte[]> entry : modMap.entrySet()) {
      ModificationTarget mt = entry.getKey();
      assertEquals(commOne.getUuid(), mt.uuid);
      byte[] mods = entry.getValue();
      Communication mergedComm = commOne.toBuilder().mergeFrom(mods).build();
      assertEquals(commOne.toBuilder().addSectionSegmentation(ssToAppend).build(), mergedComm);
    }
  }

}
