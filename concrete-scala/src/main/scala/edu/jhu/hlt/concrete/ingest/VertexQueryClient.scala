package edu.jhu.hlt.concrete.ingest

import edu.jhu.hlt.concrete.Concrete.Vertex
import edu.jhu.hlt.concrete.ConcreteException
import edu.jhu.hlt.concrete.kb._
import com.basho.riak.client._
import org.slf4j.LoggerFactory

import com.basho.riak.client.raw.pbc.PBClientConfig
import com.basho.riak.client.raw.http.HTTPClientConfig

import scala.collection.JavaConversions._
import scala.concurrent._
import ExecutionContext.Implicits.global

class VertexQueryClient {
  private val conf = RiakConfig.getPbcClusterConfig
  private val client = RiakFactory.newClient(conf)
  private val bucket = client.fetchBucket(RiakConfig.getVertexBucket).execute
  private lazy val keyArray = fetchKeys("tac-kb-09")

  def fetchVertexByIdFuture(datasetId: String) : Future[Vertex] = future {
    fetchVertexById(datasetId)
  }

  def fetchVertexById(datasetId: String) : Vertex = {
    val result = bucket.fetch(datasetId).execute()
    Vertex.parseFrom(result.getValue)
  }

  def sampleVertices(percentage: Double) = percentage match {
      case o if o > 1.0 => throw new ConcreteException("Can't get over 100% of the data")
      case u if u < 0.0 => throw new ConcreteException("Can't get less than 0% of the data")
      case _ => {
        val sample = (keyArray.size * percentage).toInt
        val sliced = keyArray.slice(0, sample)
        sliced.map { kbId =>
          fetchVertexByIdFuture(kbId)
        }
      }
  }

  private def fetchKeys(keySetName: String) : Array[String] = {
    val idBucket = client.fetchBucket(RiakConfig.getTacIdBucket).execute
    val keyString = new String(idBucket.fetch(keySetName).execute().getValue)
    keyString.split(',')
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
    val result = ingester.fetchVertexByIdFuture("E0005205")
    result onSuccess {
      case v => logger.info("Got result: " + v.getName(0).getValue)
    }

    ingester.close
  }
}
