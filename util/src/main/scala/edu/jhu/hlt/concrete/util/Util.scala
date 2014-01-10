/**
 *
 */
package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.{Uuid, TextSpan, Communication}
import java.io.{BufferedInputStream, FileInputStream, File}
import org.apache.thrift.TDeserializer
import org.apache.thrift.protocol.TBinaryProtocol
import com.twitter.scrooge.BinaryThriftStructSerializer

import scala.collection.JavaConversions._
import scala.language.postfixOps

/**
  * Utility code for working with Concrete objects.
  *
  * @author max
  */
object ConcreteUtil {

  /**
    * Use a default deserialization strategy to return a
    * `Communication` object from a Java `File`.
    *
    * @param file a Java `File` object representing a serialized Concrete `Communication`.
    * @return a `Communication` object.
    */
  def deserializeFile(file: File) : Communication = {
    val bis = new BufferedInputStream(new FileInputStream(file))
    try {
      BinaryThriftStructSerializer(Communication).fromInputStream(bis)
    } finally {
      bis.close
    }
  }

  /**
    * Use a default deserialization strategy to return a
    * `Communication` object from a file path.
    *
    * @param path The path to a serialized Concrete `Communication`.
    * @return a `Communication` object.
    */
  def deserializeFile(path: String) : Communication = {
    deserializeFile(new File(path))
  }

  /**
    * Return a randomly generated Concrete `Uuid` object.
    */
  def generateUUID : Uuid = {
    Uuid(java.util.UUID.randomUUID().toString)
  }
}

/**
  * A wrapper around `TextSpan` that provides additional utility
  * methods for working with TextSpan objects.
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
