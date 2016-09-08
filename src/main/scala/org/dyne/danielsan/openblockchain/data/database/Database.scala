package org.dyne.danielsan.openblockchain.data.database

import com.websudos.phantom.db.DatabaseImpl
import com.websudos.phantom.dsl._
import org.dyne.danielsan.openblockchain.data.connector.Config
import org.dyne.danielsan.openblockchain.data.entity._
import org.dyne.danielsan.openblockchain.data.model._

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 05/02/2016.
  */
class Database(val keyspace: KeySpaceDef) extends DatabaseImpl(keyspace) {

  def insertBlocks(blocks: List[Block]) = {
    ChainDatabase.blocks.insertMany(blocks)
  }

  def insertBlocksByHash(blocks: List[BlockByHash]) = {
    ChainDatabase.blocksByHash.insertMany(blocks)
  }

  def insertTransactions(txs: List[Transaction]) = {
    ChainDatabase.transactions.insertMany(txs)
  }

  def getBlock(height: Int): Future[Option[Block]] = {
    ChainDatabase.blocks.getByHeight(height)
  }

  def getTransaction(txId: String): Future[Option[Transaction]] = {
    ChainDatabase.transactions.getByTxid(txId)
  }

  def getVisualization(id: String, period: String, unit: String) = {
    ChainDatabase.visualizations.getVisualization(id, period, unit)
  }

  object blocks extends ConcreteBlockModel with keyspace.Connector

  object blocksByHash extends ConcreteBlockByHashModel with keyspace.Connector

  object transactions extends ConcreteTransactionModel with keyspace.Connector

  object visualizations extends ConcreteVisualizationModel with keyspace.Connector

}

object ChainDatabase extends Database(Config.keySpaceDefinition)
