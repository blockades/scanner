package org.dyne.danielsan.openblockchain.data.connector

import com.datastax.driver.core.{Cluster, HostDistance, PoolingOptions, SocketOptions}
import com.websudos.phantom.connectors._

/**
  * Created by dan_mi_sun on 15/03/2016.
  */
object Config {

  def clusterBuilder(builder: Cluster.Builder): Cluster.Builder = {
    val hostsNum = sys.env.get("CASSANDRA_HOSTS_NUM").map(_.toInt).getOrElse(0)
    val hosts = if (hostsNum > 0) {
      1 to hostsNum map {
        index => sys.env("CASSANDRA_HOST_" + index)
      }
    } else {
      Seq(sys.env.getOrElse("CASSANDRA_HOST", "localhost"))
    }

    val socketOptions = new SocketOptions()
      .setConnectTimeoutMillis(120 * 1000)
      .setReadTimeoutMillis(120 * 1000)

    val poolingOptions = new PoolingOptions()
      .setMaxRequestsPerConnection(HostDistance.LOCAL, 1024)
      .setMaxRequestsPerConnection(HostDistance.REMOTE, 1024)
      .setPoolTimeoutMillis(10 * 60 * 1000)

    builder
      .addContactPoints(hosts: _*)
      .withSocketOptions(socketOptions)
      .withPoolingOptions(poolingOptions)
  }

  val keySpaceDefinition: KeySpaceDef = new KeySpaceBuilder(clusterBuilder).keySpace("openblockchain")

}
