/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.communications;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.metadata.tools.TooledMetadataConverter;
import edu.jhu.hlt.concrete.safe.communications.SafeCommunication;
import edu.jhu.hlt.concrete.safe.metadata.SafeAnnotationMetadata;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

/**
 * Class that converts {@link SafeCommunication} objects to Concrete {@link Communication}
 * objects, which can then be added to.
 */
public class SafeCommunicationConverter {
  /**
   * @param sc a {@link SafeCommunication}
   * @return a {@link Communication} with required fields filled in
   */
  public static Communication toCommunication(SafeCommunication sc) {
    SafeAnnotationMetadata md = sc.getMetadata();
    AnnotationMetadata cmd = TooledMetadataConverter.convert(md);

    Communication cc = new Communication()
        .setId(sc.getId())
        .setUuid(UUIDFactory.fromJavaUUID(sc.getUUID()))
        .setType(sc.getType())
        .setMetadata(cmd);
    return cc;
  }
}
