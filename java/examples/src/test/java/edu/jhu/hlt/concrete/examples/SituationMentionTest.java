/**
 * 
 */
package edu.jhu.hlt.concrete.examples;

import java.util.UUID;

import org.junit.Test;

import edu.jhu.hlt.concrete.SituationMention;
import edu.jhu.hlt.concrete.SituationType;
import edu.jhu.hlt.concrete.StateType;

/**
 * @author max
 *
 */
public class SituationMentionTest {

  /**
   * 
   */
  public SituationMentionTest() {
    // TODO Auto-generated constructor stub
  }

  @Test
  public void testSituationMention() throws Exception {
    SituationMention st = new SituationMention();
    st.setUuid(UUID.randomUUID().toString());
    st.situationType = SituationType.STATE;
    st.confidence = 0.95F;
    st.stateType = StateType.EMP_ORG_MEMBER_OF_GROUP_STATE;
    st.text = "He was employed by IBM in 2009";
    // st.tokens = ...
  }
}
