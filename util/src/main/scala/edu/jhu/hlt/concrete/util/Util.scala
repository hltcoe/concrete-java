/**
 *
 */
package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.{UUID, TextSpan}

/**
  * Utility code for working with Concrete objects.
  * 
  * @author max
  */
object ConcreteUtil {
  /**
    * Return a randomly generated Concrete `UUID` object. 
    */
  def generateUUID : UUID = {
    val jUuid = java.util.UUID.randomUUID()
    new UUID(jUuid.getMostSignificantBits, jUuid.getLeastSignificantBits)
  }
}

/**
  * A wrapper around [[TextSpan]] that provides additional utility methods for working with TextSpan objects. 
  */
class SuperTextSpan(textSpan: TextSpan) {
  /**
    * Returns a `String` that represents the text of the original document that this [[TextSpan]] object points to.
    */
  def getSpanText(text: String) : String = {
    text.substring(textSpan.getStart, textSpan.getEnding)
  }
}

/**
  * A wrapper around `UUID` that provides a method for getting a `String` representation of a `UUID`.
  */
class SuperUUID(uuid: UUID) {
  /**
    * Returns a `String` representation of the `UUID` encapsulated by this [[SuperUUID]] object.
    */
  def getUUIDString : String = {
    new java.util.UUID(uuid.high, uuid.low).toString
  }
}
