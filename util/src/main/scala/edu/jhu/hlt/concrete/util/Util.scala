/**
 *
 */
package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.{UUID, TextSpan}

/**
 * @author max
 *
 */
object ConcreteUtil {
  def generateUUID : UUID = {
    val jUuid = java.util.UUID.randomUUID()
    new UUID(jUuid.getMostSignificantBits, jUuid.getLeastSignificantBits)
  }
  
  def getTextSpanText (text: String, span: TextSpan) : String = {
    text.substring(span.getStart(), span.getEnding())
  }
}
