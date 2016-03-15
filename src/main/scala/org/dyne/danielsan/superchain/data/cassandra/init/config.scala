package org.dyne.danielsan.superchain.data.cassandra.init

import com.websudos.phantom.connectors.{ContactPoint, KeySpace, KeySpaceBuilder, KeySpaceDef}


/**
  * Created by dan_mi_sun on 15/03/2016.
  */
object Config {

  val keySpace = KeySpace("superchain")
  val contactPoint: KeySpaceBuilder = ContactPoint(host = "localhost", port = ContactPoint.DefaultPorts.live)
  val keySpaceDefinition: KeySpaceDef = contactPoint.keySpace(keySpace.name)

}
