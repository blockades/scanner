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

  def saveOrUpdateBlockTransactionCount(counts: BlockTransactionCounts): Future[ResultSet] = {
    Batch.logged
    ChainDatabase.btc.increment(counts)
  }

  def getBlockTransactionCountByHash(hash: String): Future[Option[Long]] = {
    ChainDatabase.btc.getCount(hash)
  }

  def saveOrUpdateBlockOpReturnTransactionCount(counts: BlockOpReturnTransactionCounts): Future[ResultSet] = {
    Batch.logged
    ChainDatabase.bortc.increment(counts)
  }

  def getBlockOpReturnTransactionCountByHash(hash: String): Future[Option[Long]] = {
    ChainDatabase.bortc.getCount(hash)
  }

  def listAllBlocks = {
    Batch.logged
    ChainDatabase.block.listAll
  }

  def listAllTransactions = {
    Batch.logged
    ChainDatabase.tx.listAll
  }

  def getBlockByHash(id: String): Future[Option[Block]] = {
    ChainDatabase.block.getByHash(id)
  }

  def getTransactionByTxid(id: String): Future[Option[Transaction]] = {
    ChainDatabase.tx.getByTxid(id)
  }


  object block extends ConcreteBlocksModel with keyspace.Connector

  object tx extends ConcreteTransactionsModel with keyspace.Connector

  object btc extends ConcreteBlockTransactionCountsModel with keyspace.Connector

  object bortc extends ConcreteBlockOpReturnTransactionCountsModel with keyspace.Connector

}

object ChainDatabase extends Database(Config.keySpaceDefinition)