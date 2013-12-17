/**
 *
 */
package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.{UUID, TextSpan}
import scala.language.implicitConversions

/**
 * @author max
 *
 */
object ConcreteUtil {
  def generateUUID : UUID = {
    val jUuid = java.util.UUID.randomUUID()
    new UUID(jUuid.getMostSignificantBits, jUuid.getLeastSignificantBits)
  }
}

class SuperTextSpan(textSpan: TextSpan) {
  def getSpanText(text: String) : String = {
    text.substring(textSpan.getStart, textSpan.getEnding)
  }

  implicit def superTextSpan(textSpan: TextSpan) = new SuperTextSpan(textSpan)
}


