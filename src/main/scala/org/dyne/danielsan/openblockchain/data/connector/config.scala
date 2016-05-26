package org.dyne.danielsan.openblockchain.data.connector

import com.websudos.phantom.connectors.{ContactPoint, KeySpace, KeySpaceBuilder, KeySpaceDef}


/**
  * Created by dan_mi_sun on 15/03/2016.
  */
object Config {

  val keySpace = KeySpace("openblockchain")
  val contactPoint: KeySpaceBuilder = ContactPoint(host = "188.166.168.250", port = ContactPoint.DefaultPorts.live)
  val keySpaceDefinition: KeySpaceDef = contactPoint.keySpace(keySpace.name)

}
