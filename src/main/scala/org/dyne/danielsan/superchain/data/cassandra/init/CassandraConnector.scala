package org.dyne.danielsan.superchain.data.cassandra.init

import com.websudos.phantom.connectors.{KeySpace, SimpleConnector}

trait CassandraConnector extends SimpleConnector with KeyspaceDefinition

trait KeyspaceDefinition {
  implicit val keySpace = KeySpace("superchain")
}
