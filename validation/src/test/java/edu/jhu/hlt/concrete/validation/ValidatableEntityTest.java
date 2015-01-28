/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.validation;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Entity;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.util.ConcreteUUIDFactory;

/**
 * @author max
 *
 */
public class ValidatableEntityTest {

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

  @Test
  public void badUUID() {
    Entity e = new Entity();
    
    e.setUuid(new UUID("foo"));
    e.setMentionIdList(new ArrayList<UUID>());
    e.setType("bar");
    
    assertFalse(new ValidatableEntity(e).isValid());
  }
  
  @Test
  public void badType() {
    Entity e = new Entity();
    
    e.setUuid(new ConcreteUUIDFactory().getConcreteUUID());
    List<UUID> uuidList = new ArrayList<>();
    uuidList.add(new ConcreteUUIDFactory().getConcreteUUID());
    
    e.setMentionIdList(uuidList);
    e.setType("");
    
    assertFalse(new ValidatableEntity(e).isValid());
  }
  
  @Test
  public void noIDs() {
    Entity e = new Entity();
    
    e.setUuid(new ConcreteUUIDFactory().getConcreteUUID());
    List<UUID> uuidList = new ArrayList<>();
    e.setMentionIdList(uuidList);
    e.setType("asdf");
    
    assertFalse(new ValidatableEntity(e).isValid());
  }
}
