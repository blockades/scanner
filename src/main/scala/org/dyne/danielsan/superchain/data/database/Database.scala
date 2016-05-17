package org.dyne.danielsan.superchain.data.database

import com.websudos.phantom.db.DatabaseImpl
import com.websudos.phantom.dsl._
import org.dyne.danielsan.superchain.data.connector.Config
import org.dyne.danielsan.superchain.data.entity._
import org.dyne.danielsan.superchain.data.model._

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
//  //this needs some work - just want to make sure that the table is updated
//  def insertBlockTransactionCounts(btc: BlockTransactionCounts) = {
//    Batch.logged
//      .add(ChainDatabase.btc.increment(btc))
//      .future()
//  }
  //this needs some work
  def saveOrUpdate(counts: BlockTransactionCounts): Future[ResultSet] = {
    for {
      total <- ChainDatabase.btc.increment(counts)
//      byArtist <- ChainDatabase.songsByArtistsModel.store(counts)
    } yield total
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

}

object ChainDatabase extends Database(Config.keySpaceDefinition)