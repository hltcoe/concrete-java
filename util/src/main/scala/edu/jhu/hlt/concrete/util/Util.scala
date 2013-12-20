/**
 *
 */
package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.{UUID, TextSpan}

/**
  * Utility code for working with Concrete.
  * 
  * @author max
  */
object ConcreteUtil {
  /**
    * Return a randomly generated Concrete [[UUID]] object. 
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
    * Returns a [[String]] that represents the text of the original document that this [[TextSpan]] object points to.
    */
  def getSpanText(text: String) : String = {
    text.substring(textSpan.getStart, textSpan.getEnding)
  }
}


