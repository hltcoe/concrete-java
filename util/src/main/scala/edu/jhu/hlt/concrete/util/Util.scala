/**
 *
 */
package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.{UUID, TextSpan, Communication}
import java.io.{BufferedInputStream, FileInputStream, File}
import org.apache.thrift.TDeserializer
import org.apache.thrift.protocol.TBinaryProtocol

import scala.collection.JavaConversions._
import scala.language.postfixOps

/**
  * Utility code for working with Concrete objects.
  * 
  * @author max
  */
object ConcreteUtil {
  private val DefaultDeserializer = new TDeserializer(new TBinaryProtocol.Factory())

  /**
    * Use a default deserialization strategy to return a `Communication` object from a Java `File`.
    * @param file a Java `File` object representing a serialized Concrete `Communication`.
    * @return a `Communication` object.
    */
  def deserializeFile(file: File) : Communication = {
    val comm = new Communication()
    val bis = new BufferedInputStream(new FileInputStream(file))
    val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
    bis.close
    DefaultDeserializer.deserialize(comm, byteArray)
    comm
  }

  /**
    * Use a default deserialization strategy to return a `Communication` object from a file path.
    * @param path The path to a serialized Concrete `Communication`.
    * @return a `Communication` object.
    */
  def deserializeFile(path: String) : Communication = {
    deserializeFile(new File(path))
  }

  /**
    * Return a randomly generated Concrete `UUID` object. 
    */
  def generateUUID : UUID = {
    val jUuid = java.util.UUID.randomUUID()
    new UUID(jUuid.getMostSignificantBits, jUuid.getLeastSignificantBits)
  }
}

/**
  * A wrapper around `TextSpan` that provides additional utility methods for working with TextSpan objects. 
  */
class SuperTextSpan(textSpan: TextSpan) {
  /**
    * Returns a `String` that represents the text of the original document that this `TextSpan` object points to.
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
