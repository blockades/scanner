package org.dyne.danielsan.openblockchain.data.connector

import com.websudos.phantom.connectors._

/**
  * Created by dan_mi_sun on 15/03/2016.
  */
object Config {

  val hostsNum = sys.env.get("CASSANDRA_HOSTS_NUM").map(_.toInt).getOrElse(0)

  val hosts = if (hostsNum > 0) {
    1 to hostsNum map {
      index => sys.env("CASSANDRA_HOST_" + index)
    }
  } else {
    Seq(sys.env.getOrElse("CASSANDRA_HOST", "localhost"))
  }
  val port = ContactPoint.DefaultPorts.live

  val keySpace = KeySpace("openblockchain")
  val contactPoint: KeySpaceBuilder = ContactPoints(hosts, port)
  val keySpaceDefinition: KeySpaceDef = contactPoint.keySpace(keySpace.name)

}
