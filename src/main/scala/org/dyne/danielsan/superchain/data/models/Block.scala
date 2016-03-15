package org.dyne.danielsan.superchain.data.models

import com.datastax.driver.core.{ResultSet, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.iteratee.Iteratee

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 27/02/2016.
  */

/*
$ bitcoin-cli getblock 00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048


{
"hash" : "00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048",
"confirmations" : 299191,
"size" : 215,
"height" : 1,
"version" : 1,
"merkleroot" : "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098",
"tx" : [
"0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098"
],
"time" : 1231469665,
"nonce" : 2573394689,
"bits" : "1d00ffff",
"difficulty" : 1.00000000,
"chainwork" : "0000000000000000000000000000000000000000000000000000000200020002",
"previousblockhash" : "000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",
"nextblockhash" : "000000006a625f06636b8bb6ac7b960a8d03705d1ace08b1a19da3fdcc99ddbd"
}
*/

case class Block(hash: String,
                 confirmations: Int,
                 size: Int,
                 height: Int,
                 version: Int,
                 merkleroot: String,
                 tx: List[String],
                 time: Long,
                 nonce: Long,
                 bits: String,
                 difficulty: Float,
                 chainwork: String,
                 previousblockhash: String,
                 nextblockhash: String)


sealed class BlockColumnFamily extends CassandraTable[BlockColumnFamily, Block] {

  override def fromRow(row: Row): Block = {
    Block(
      hash(row),
      confirmations(row),
      size(row),
      height(row),
      version(row),
      merkleroot(row),
      tx(row),
      time(row),
      nonce(row),
      bits(row),
      difficulty(row),
      chainwork(row),
      previousblockhash(row),
      nextblockhash(row)
    )
  }

    object hash extends StringColumn(this) with PartitionKey[String]

    object confirmations extends IntColumn(this) with ClusteringOrder[Int] with Descending

    object size extends IntColumn(this) with ClusteringOrder[Int] with Descending

    object height extends IntColumn(this) with ClusteringOrder[Int] with Descending

    object version extends IntColumn(this) with ClusteringOrder[Int] with Descending

    object merkleroot extends StringColumn(this) with ClusteringOrder[String] with Descending

    object tx extends ListColumn[BlockColumnFamily, Block, String](this)

    object time extends LongColumn(this) with ClusteringOrder[Long] with Descending

    object nonce extends LongColumn(this) with ClusteringOrder[Long] with Descending

    object bits extends StringColumn(this) with ClusteringOrder[String] with Descending

    object difficulty extends FloatColumn(this) with ClusteringOrder[Float] with Descending

    object chainwork extends StringColumn(this) with ClusteringOrder[String] with Descending

    object previousblockhash extends StringColumn(this)

    object nextblockhash extends StringColumn(this)

  }

  abstract class BlockTable extends BlockColumnFamily with RootConnector {

    override val tableName = "blocks"

    def insertNew(block: Block): Future[ResultSet] = insertNewRecord(block).future()

    def insertNewRecord(block: Block) = {
      insert
        .value(_.hash, block.hash)
        .value(_.confirmations, block.confirmations)
        .value(_.size, block.size)
        .value(_.height, block.height)
        .value(_.version, block.version)
        .value(_.merkleroot, block.merkleroot)
        .value(_.tx, block.tx)
        .value(_.time, block.time)
        .value(_.nonce, block.nonce)
        .value(_.bits, block.bits)
        .value(_.difficulty, block.difficulty)
        .value(_.chainwork, block.chainwork)
        .value(_.previousblockhash, block.previousblockhash)
        .value(_.nextblockhash, block.nextblockhash)
    }

    def listAll = {
      select.fetchEnumerator() run Iteratee.collect()
    }

    def destroy(hash: String): Future[ResultSet] = {
      delete.where(_.hash eqs hash).future()
    }

//    def updateBlockEntry(block: Block) = {
//      update.where(_.hash eqs block.hash)
//        .modify(_.confirmations setTo block.confirmations)
//        .and(_.size setTo block.size)
//        .and(_.height setTo block.height)
//        .and(_.version setTo block.version)
//        .and(_.merkleroot setTo block.merkleroot)
//        .and(_.tx setTo block.tx)
//        .and(_.time setTo block.time)
//        .and(_.nonce setTo block.nonce)
//        .and(_.bits setTo block.bits)
//        .and(_.difficulty setTo block.difficulty)
//        .and(_.chainwork setTo block.chainwork)
//        .and(_.previousblockhash setTo block.previousblockhash)
//        .and(_.nextblockhash setTo block.nextblockhash)
//        .consistencyLevel_=(ConsistencyLevel.ALL)
//        .future()
//      getByHash(block.hash)
//    }

    def getByHash(hash: String): Future[Option[Block]] = {
      select.where(_.hash eqs hash).one()
    }

  }