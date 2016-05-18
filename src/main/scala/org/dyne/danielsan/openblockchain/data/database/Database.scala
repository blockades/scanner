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
//  //this needs some work - just want to make sure that the table is updated
//  def insertBlockTransactionCounts(btc: BlockTransactionCounts) = {
//    Batch.logged
//      .add(ChainDatabase.btc.increment(btc))
//      .future()
//  }
  //this needs some work
  // how to get the hash
  // how to get the time
//  def saveOrUpdateBlockTransactionCount(counts: BlockTransactionCounts): Future[ResultSet] = {
//    for {
//      //What are we trying to do here?
//      //Each time a Block is saved we also want it to check how many transactions it contained
//      ???
//    } yield ???
//  }
// http://stackoverflow.com/questions/37245031/how-to-implement-cassandra-counter-columns-with-phantom-dsl/37256783#37256783
//  def saveOrUpdate(song: Song): Future[ResultSet] = {
//    for {
//      byId <- database.songsModel.store(songs)
//      byArtist <- database.songsByArtistsModel.store(songs)
//      counter <- database.artistSongsCounter.increment(song.artist)
//    } yield byArtist
//  }

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