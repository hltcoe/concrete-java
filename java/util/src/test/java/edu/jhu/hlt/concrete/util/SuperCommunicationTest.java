/**
 * 
 */
package edu.jhu.hlt.concrete.util;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.SectionSegmentation;

/**
 * @author max
 *
 */
public class SuperCommunicationTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.util.SuperCommunication#hasSectionSegmentations()}.
   */
  @Test
  public void hasSectionSegmentationsFalseForUnsetSectionSegs() {
    Communication comm = mock(Communication.class);
    when(comm.isSetSectionSegmentations()).thenReturn(false);
    
    assertFalse(new SuperCommunication(comm).hasSectionSegmentations());
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.util.SuperCommunication#hasSectionSegmentations()}.
   */
  @Test
  public void hasSectionSegmentationsFalseForNoSectionSegs() {
    Communication comm = mock(Communication.class);
    when(comm.isSetSectionSegmentations()).thenReturn(true);
    when(comm.getSectionSegmentationsSize()).thenReturn(0);
    
    assertFalse(new SuperCommunication(comm).hasSectionSegmentations());
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.util.SuperCommunication#hasSectionSegmentations()}.
   */
  @Test
  public void hasSectionSegmentationsTrueForOverZeroSectionSegs() {
    Communication comm = mock(Communication.class);
    when(comm.isSetSectionSegmentations()).thenReturn(true);
    when(comm.getSectionSegmentationsSize()).thenReturn(1);
    
    assertFalse(new SuperCommunication(comm).hasSectionSegmentations());
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.concrete.util.SuperCommunication#hasSections()}.
   */
  @Test
  public void hasSectionsFalseWhenUnsetSections() {
    SectionSegmentation ss = mock(SectionSegmentation.class);
    when(ss.isSetSectionList()).thenReturn(false);
    List<SectionSegmentation> list = new ArrayList<SectionSegmentation>();
    list.add(ss);
    
    Communication comm = mock(Communication.class);
    when(comm.isSetSectionSegmentations()).thenReturn(true);
    when(comm.getSectionSegmentationsSize()).thenReturn(1);
    when(comm.getSectionSegmentations()).thenReturn(list);
    
    assertFalse(new SuperCommunication(comm).hasSections());
  }

}
