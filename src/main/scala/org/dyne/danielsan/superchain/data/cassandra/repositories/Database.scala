package org.dyne.danielsan.superchain.data.cassandra.repositories

import com.websudos.phantom.db.DatabaseImpl
import com.websudos.phantom.dsl._
import org.dyne.danielsan.superchain.data.cassandra.init.Config
import org.dyne.danielsan.superchain.data.models._
import org.dyne.danielsan.superchain.data.entity._



/**
  * Created by dan_mi_sun on 05/02/2016.
  */

// the next challenge here is to find out what the exception is. How to insert something which has as many nested layers as the transaction has. The Block is not the same as that in this sense... so I am not even sure if the connector has been implemented correctly.

class Database(val keyspace: KeySpaceDef) extends DatabaseImpl(keyspace) {

  def insertBlock(block: Block) = {
    Batch.logged
      .add(ChainDatabase.block.insertNewRecord(block))
      .future()
  }

  def insertTransaction(tx: Transaction) = {
    Batch.logged
      .add(ChainDatabase.tx.insertNewTransaction(tx))
      .future()
  }

  def listAllBlocks(block: Block) = {
    Batch.logged
  }

  object block extends BlockTable with keyspace.Connector

  object tx extends ConcreteTransactionsModel with keyspace.Connector

}

object ChainDatabase extends Database(Config.keySpaceDefinition)