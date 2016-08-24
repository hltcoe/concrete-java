/*
 *
 */
package edu.jhu.hlt.concrete.miscommunication;

import edu.jhu.hlt.utilt.uuid.UUIDable;

/**
 *
 */
public interface CommunicationAnnotationWithCommunicationID extends UUIDable {
  public NonEmptyString getCommunicationID();
}
