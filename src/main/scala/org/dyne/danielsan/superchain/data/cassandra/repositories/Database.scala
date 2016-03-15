package org.dyne.danielsan.superchain.data.cassandra.repositories

import com.websudos.phantom.db.DatabaseImpl
import com.websudos.phantom.dsl._
import org.dyne.danielsan.superchain.data.cassandra.init.Config
import org.dyne.danielsan.superchain.data.models.{Block, BlockTable}

/**
  * Created by dan_mi_sun on 05/02/2016.
  */

class Database(val keyspace: KeySpaceDef) extends DatabaseImpl(keyspace) {

  def insertBlock(block: Block) = {
    Batch.logged
      .add(ChainDatabase.block.insertNewRecord(block))
      .future()
  }

  object block extends BlockTable with keyspace.Connector


}

object ChainDatabase extends Database(Config.keySpaceDefinition)