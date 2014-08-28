/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import concrete.util.data.ConcreteFactory;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.SuperCommunication;

/**
 * @author max
 *
 */
public class SuperCommunicationTest {

  ConcreteFactory cf;
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    cf = new ConcreteFactory();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.communications.SuperCommunication#hasSectionSegmentations()}.
   */
  @Test
  public void hasSectionSegmentationsFalseForUnsetSectionSegs() {
    Communication comm = mock(Communication.class);
    when(comm.isSetSectionSegmentationList()).thenReturn(false);
    
    assertFalse(new SuperCommunication(comm).hasSectionSegmentations());
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.communications.SuperCommunication#hasSectionSegmentations()}.
   */
  @Test
  public void hasSectionSegmentationsFalseForNoSectionSegs() {
    Communication comm = cf.randomCommunication();
    assertFalse(new SuperCommunication(comm).hasSectionSegmentations());
  }

  /**
   * Test method for {@link edu.jhu.hlt.concrete.communications.SuperCommunication#hasSectionSegmentations()}.
   */
  @Test
  public void hasSectionSegmentationsTrueForOverZeroSectionSegs() {
    Communication comm = cf.randomCommunication();
    SectionSegmentation ss = new SectionSegmentation()
      .setUuid(new ConcreteUUIDFactory().getConcreteUUID());
    Section s = new Section()
      .setUuid(new ConcreteUUIDFactory().getConcreteUUID())
      .setKind("Other")
      .setTextSpan(new TextSpan(0, comm.getText().length()));
    ss.addToSectionList(s);
    comm.addToSectionSegmentationList(ss);
    
    assertTrue(new SuperCommunication(comm).hasSectionSegmentations());
  }
  
  /**
   * Test method for {@link edu.jhu.hlt.concrete.communications.SuperCommunication#hasSections()}.
   */
  @Test
  public void hasSectionsFalseWhenUnsetSections() {
    SectionSegmentation ss = mock(SectionSegmentation.class);
    when(ss.isSetSectionList()).thenReturn(false);
    List<SectionSegmentation> list = new ArrayList<SectionSegmentation>();
    list.add(ss);
    
    Communication comm = cf.randomCommunication();
    comm.addToSectionSegmentationList(ss);
    
    assertFalse(new SuperCommunication(comm).hasSections());
  }

//  @Test
//  public void entityMentionSetCaching() {
//    Communication c = new ConcreteFactory().randomCommunication();
//    EntityMentionSet ems = new EntityMentionSet();
//    ems.setUuid(new ConcreteUUIDFactory().getConcreteUUID());
//    
//  }
}
