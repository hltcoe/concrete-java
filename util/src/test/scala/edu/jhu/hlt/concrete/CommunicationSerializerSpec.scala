/**
  *  Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights reserved.
  *  This software is released under the 2-clause BSD license.
  *  See LICENSE in the project root directory.
  */
package edu.jhu.hlt.concrete

import org.specs2.mutable._
import edu.jhu.hlt.concrete._
import scala.collection.JavaConverters._

class SerializerSpec extends Specification {
  "CommunicationSerializer" should {
    "Successfully serialize/deserialize byte arrays" in {
      val comm = Util.generateCommunication
      val md = new AnnotationMetadata()
      md.tool = "Rebar Unit Tests"
      val lid = new LanguageIdentification
      val uuidStr = Util.randomUuid
      lid.uuid = uuidStr
      lid.metadata = md
      lid.languageToProbabilityMap = Map("eng" -> new java.lang.Double(.99)).asJava
      comm.lid = lid

      val bytez = CommunicationSerializer.toBytes(comm)

      val deserComm = CommunicationSerializer.fromBytes(bytez)
      deserComm.lid.uuid must beEqualTo(uuidStr)
    }
  }
}
