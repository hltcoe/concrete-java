/**
 *
 */
package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.{TextSpan, Communication, CommunicationType}

/**
  * Utility code for working with Concrete objects.
  *
  * @author max
  */
object Util {
  import scala.util.Random
  private val r = new Random

  /**
    * A function that generates a mock `Communication`,
    * suitable for testing.
    * @return a `Communication` with a random doc ID, uuid,
    a type of `CommunicationType.OTHER`, and some sample text.
    */
  def generateCommunication : Communication = {
    val rInt = r.nextInt
    val docIdStr = s"Communication_$rInt"
    val uuidStr = java.util.UUID.randomUUID.toString
    val text = "Lorem Ipsum. This is some sample text!"
    new Communication(docIdStr, uuidStr, CommunicationType.OTHER, text)
  }
}

/**
  * A wrapper around `TextSpan` that provides additional utility
  * methods for working with `TextSpan` objects.
  */
class SuperTextSpan(textSpan: TextSpan) {
  /**
    * Returns a `String` that represents the text of the original
    * document that this `TextSpan` object points to.
    */
  def getSpanText(text: String) : String = {
    text.substring(textSpan.start, textSpan.ending)
  }
}

/**
  * A wrapper around `Communication` that provides additional utility
  * methods.
  */
class SuperCommunication(comm: Communication) {

}
