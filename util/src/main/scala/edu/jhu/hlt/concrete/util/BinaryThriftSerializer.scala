/**
 *
 */
package edu.jhu.hlt.concrete.util

import edu.jhu.hlt.concrete.{TextSpan, Communication, CommunicationType}
import java.io.{BufferedInputStream, FileInputStream, File, InputStream}
import org.apache.thrift.{TDeserializer, TSerializer, TBase, TFieldIdEnum}
import org.apache.thrift.protocol.{TBinaryProtocol, TCompactProtocol, TProtocol,
  TProtocolFactory, TSimpleJSONProtocol}
import org.apache.thrift.transport.TIOStreamTransport

import scala.language.postfixOps

/**
  * A convenient wrapper around the thrift de/serializer,
  * which provides some convenience when working with
  * de/serializing Thrift `Communication` objects.
  *
  * @author max
  */
object CommunicationSerializer {
  val protocolFactory = new TBinaryProtocol.Factory
  val ser = new TSerializer(protocolFactory)
  val deser = new TDeserializer(protocolFactory)

  def toBytes (comm: Communication) : Array[Byte] = {
    ser.serialize(comm)
  }

  def fromBytes(bytes: Array[Byte]) : Communication = {
    val comm = new Communication
    deser.deserialize(comm, bytes)
    comm
  }

  /**
    * Use a default deserialization strategy to return a
    * `Communication` object from a Java `File`.
    *
    * @param file a Java `File` object representing a serialized Concrete `Communication`.
    * @return a `Communication` object.
    */
  def fromFile(file: File) : Communication = {
    val bis = new BufferedInputStream(new FileInputStream(file))
    try {
      fromBytes(Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray)
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
  def fromFile(path: String) : Communication = {
    fromFile(new File(path))
  }
}
