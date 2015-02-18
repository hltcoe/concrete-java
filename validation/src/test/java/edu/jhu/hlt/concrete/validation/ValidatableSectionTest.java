/*
 * Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.communications.SuperCommunication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 *
 */
public class ValidatableSectionTest extends AbstractValidationTest {

  public Section generateSection() {
    Section s = new Section()
      .setUuid(UUIDFactory.newUUID())
      .setTextSpan(new TextSpan(0, this.comm.getText().length()))
      .setKind("Passage");
    return s;
  }

  public Communication constructComm() {
    Communication cpy = new Communication(this.comm);
    cpy.addToSectionList(this.generateSection());
    return cpy;
  }

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
  public void validOK() throws ConcreteException {
    Communication v = this.constructComm();
    ValidatableSection vs = new ValidatableSection(new SuperCommunication(v).firstSection());
    assertTrue(vs.isValid());
    assertTrue(vs.validate(v));
  }

  @Test
  public void badUuid() throws ConcreteException {
    Communication v = this.constructComm();
    Section s = new SuperCommunication(v).firstSection();
    s.setUuid(new UUID("3k3pgjp3"));
    ValidatableSection vs = new ValidatableSection(s);
    assertFalse(vs.isValid());
    assertFalse(vs.validate(v));
  }

  @Test
  public void badTextSpan() throws ConcreteException {
    Communication v = this.constructComm();
    Section s = new SuperCommunication(v).firstSection();
    s.setTextSpan(new TextSpan(-1, 0));
    ValidatableSection vs = new ValidatableSection(s);
    assertFalse(vs.isValid());
    assertFalse(vs.validate(v));
  }

  @Test
  public void noKind() throws ConcreteException {
    Communication v = this.constructComm();
    Section s = new SuperCommunication(v).firstSection();
    s.unsetKind();
    ValidatableSection vs = new ValidatableSection(s);
    assertFalse(vs.isValid());
    assertFalse(vs.validate(v));
  }

  @Test
  public void misalignedTextSpan() throws ConcreteException {
    Communication v = this.constructComm();
    Section s = new SuperCommunication(v).firstSection();
    s.setTextSpan(new TextSpan(0, v.getText().length() + 5));
    ValidatableSection vs = new ValidatableSection(s);
    assertTrue(vs.isValid());
    assertFalse(vs.validate(v));
  }
}
