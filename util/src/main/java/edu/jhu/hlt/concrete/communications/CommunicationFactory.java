/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.concrete.communications;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.section.SingleSectionSegmenter;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * Class that allows for construction of semi-built {@link Communication}
 * objects.
 */
public class CommunicationFactory {

  private CommunicationFactory() {

  }

  /**
   * @return a {@link Communication} with a {@link UUID} assigned.
   */
  public static final Communication create() {
    return new Communication()
      .setUuid(UUIDFactory.newUUID());
  }

  /**
   * @param id the ID of the newly created {@link Communication} object. Must
   * be non-null and non-empty.
   * @return a {@link Communication} with both a {@link UUID} and an ID.
   * @throws ConcreteException if any input strings are null or length 0.
   */
  public static final Communication create(String id) throws ConcreteException {
    if (id == null || id.length() == 0)
      throw new ConcreteException("ID was null or length 0.");
    return create()
      .setId(id);
  }

  /**
   *
   * @param id the ID of the newly created {@link Communication} object. Must
   * be non-null and non-empty.
   * @param text the text of the new Communication. Must be non-null and non-empty.
   * @return a {@link Communication} with a {@link java.util.UUID}, ID, and text.
   * @throws ConcreteException ConcreteException if any input strings are null or length 0.
   */
  public static final Communication create(String id, String text) throws ConcreteException {
    if (text == null || text.length() == 0)
      throw new ConcreteException("Text was null or length 0.");
    return create(id)
      .setText(text);
  }

  /**
   * @param id the ID of the new Communication. Must be non-null and non-empty.
   * @param text the text of the new Communication. Must be non-null and non-empty.
   * @param sectionKind the sectionKind of the new Communication. Must be non-null and non-empty.
   * @return a {@link Communication} with a {@link java.util.UUID}, ID, text, and single {@link Section}
   * that covers the whole text.
   * @throws ConcreteException if any input strings are null or length 0.
   */
  public static final Communication create(String id, String text, String sectionKind) throws ConcreteException {
    if (sectionKind == null || sectionKind.length() == 0)
      throw new ConcreteException("sectionKind was null or length 0. Use 'other' if unsure.");
    Communication c = create(id, text);
    c.addToSectionList(SingleSectionSegmenter.createSingleSection(text, sectionKind));
    return c;
  }
}
