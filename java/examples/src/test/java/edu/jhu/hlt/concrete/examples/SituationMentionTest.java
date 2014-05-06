/**
 * 
 */
package edu.jhu.hlt.concrete.examples;

import java.util.UUID;

import org.junit.Test;

import edu.jhu.hlt.concrete.SituationMention;

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
    st.situationType = "State";
    st.confidence = 0.95F;
    st.stateType = "EMP_ORG_MEMBER_OF_GROUP_STATE";
    st.text = "He was employed by IBM in 2009";
    // st.tokens = ...
  }
}
