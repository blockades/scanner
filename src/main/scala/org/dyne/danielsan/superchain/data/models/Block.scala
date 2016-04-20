package org.dyne.danielsan.superchain.data.models

import com.datastax.driver.core.{ResultSet, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.iteratee.Iteratee

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 27/02/2016.
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

  object confirmations extends IntColumn(this)

  object size extends IntColumn(this)

  object height extends IntColumn(this)

  object version extends IntColumn(this)

  object merkleroot extends StringColumn(this)

  object tx extends ListColumn[BlockColumnFamily, Block, String](this)

  object time extends LongColumn(this)

  object nonce extends LongColumn(this)

  object bits extends StringColumn(this)

  object difficulty extends FloatColumn(this)

  object chainwork extends StringColumn(this)

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

  def getByHash(hash: String): Future[Option[Block]] = {
    select.where(_.hash eqs hash).one()
  }

}