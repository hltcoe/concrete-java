package edu.jhu.hlt.concrete.ingest

import edu.jhu.hlt.concrete.Concrete.Vertex
import edu.jhu.hlt.concrete.kb._
import com.basho.riak.client._
import org.slf4j.LoggerFactory

import com.basho.riak.client.raw.pbc.PBClientConfig
import com.basho.riak.client.raw.http.HTTPClientConfig

import scala.collection.JavaConversions._

class VertexQueryClient {
  private val conf = RiakConfig.getPbcClusterConfig
  private val client = RiakFactory.newClient(conf)
  private val bucket = client.fetchBucket(RiakConfig.getVertexBucket).execute

  def fetchVertexById(datasetId: String) : Vertex = {
    val result = bucket.fetch(datasetId).execute()
    Vertex.parseFrom(result.getValue)
  }

  def close() = {
    client.shutdown()
  }
}

object VertexQueryClient {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) = {
    logger.debug("About to init VQC...")
    val ingester = new VertexQueryClient
    logger.debug("Send request")
    val result = ingester.fetchVertexById("E0005205")
    logger.info("Got result: " + result.getName(0).getValue)
    ingester.close
  }
}
