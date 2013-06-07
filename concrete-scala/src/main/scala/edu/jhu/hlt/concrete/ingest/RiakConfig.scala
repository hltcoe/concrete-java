package edu.jhu.hlt.concrete.ingest

import com.typesafe.config._
import scala.collection.mutable.ArrayBuffer
import com.basho.riak.client.raw.pbc.PBClientConfig
import com.basho.riak.client.raw.pbc.PBClusterConfig

import com.basho.riak.client.raw.http.HTTPClientConfig
import com.basho.riak.client.raw.http.HTTPClusterConfig

object RiakConfig {
  val config = ConfigFactory.load

  def getRiakHeadNode() : String = {
    this.config.getString("riak_head_node")
  }

  def getRiakHostnames() : Array[String] = {
    this.config.getString("riak_cluster_ips").split(",")
  }

  def getRiakPort() : Integer = {
    this.config.getInt("riak_port")
  }

  def getPbcClusterConfig() : HTTPClusterConfig = {
    val clusterConfig = new HTTPClusterConfig(6)

    getRiakHostnames.foreach { host =>
      clusterConfig.addClient(new HTTPClientConfig.Builder()
        .withHost(host)
        .withPort(getRiakPort)
        .build())
    }

    clusterConfig
  }

  def getVertexBucket() : String = {
    this.config.getString("vertex_bucket")
  }

  def getTacIdBucket() : String = {
    this.config.getString("tac_id_bucket")
  }
}
