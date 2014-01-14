/**
 *
 */
package edu.jhu.hlt.concrete

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
    val uuidStr = randomUuid
    val text = "Lorem Ipsum. This is some sample text!"
    new Communication(docIdStr, uuidStr, CommunicationType.OTHER, text)
  }

  def randomUuid : String = java.util.UUID.randomUUID.toString
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
  * A wrapper around `Communication` that provides additional
  * functionality.
  */
class SuperCommunication(comm: Communication) {
  private val populated : Boolean =
    comm.id != null &
    comm.uuid != null &
    comm.`type` != null &
    comm.text != null

  lazy val valid : Boolean = populated & validUuid

  private def validUuid : Boolean = try {
    java.util.UUID.fromString(comm.uuid)
    true
  } catch {
    case _ : Exception => false
  }
}
