package org.dyne.danielsan.superchain.data.cassandra.repositories

import java.util.UUID

import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.iteratee.Iteratee
import org.dyne.danielsan.superchain.data.cassandra.init.CassandraConnector
import org.dyne.danielsan.superchain.data.models.Block

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 05/02/2016.
  */

object BlockRepository extends BlockRepository with CassandraConnector {

  def insertNewRecord(block: Block) = {
    val newId = UUID.randomUUID().toString
    insert.value(_.id, newId)
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
      .future()
    getById(newId)
  }

  def getById(id: String): Future[Option[Block]] = {
    select.where(_.id eqs id).one()
  }

  def listAll = {
    select.fetchEnumerator() run Iteratee.collect()
  }

  def destroy(id: String): Future[ResultSet] = {
    delete.where(_.id eqs id).future()
  }

  def updateChainEntry(block: Block) = {
    update.where(_.id eqs block.id)
      .modify(_.hash setTo block.hash)
      .and(_.confirmations setTo block.confirmations)
      .and(_.size setTo block.size)
      .and(_.height setTo block.height)
      .and(_.version setTo block.version)
      .and(_.merkleroot setTo block.merkleroot)
      .and(_.tx setTo block.tx)
      .and(_.time setTo block.time)
      .and(_.nonce setTo block.nonce)
      .and(_.bits setTo block.bits)
      .and(_.difficulty setTo block.difficulty)
      .and(_.chainwork setTo block.chainwork)
      .and(_.previousblockhash setTo block.previousblockhash)
      .and(_.nextblockhash setTo block.nextblockhash)
      .future()
    getById(block.id)
  }
}

sealed class BlockRepository extends CassandraTable[BlockRepository, Block] {

  object id extends StringColumn(this) with PartitionKey[String]

  object hash extends StringColumn(this)

  object confirmations extends IntColumn(this)

  object size extends IntColumn(this)

  object height extends IntColumn(this)

  object version extends IntColumn(this)

  object merkleroot extends StringColumn(this)

  object tx extends ListColumn[BlockRepository, Block, String](this)

  object time extends LongColumn(this)

  object nonce extends LongColumn(this)

  object bits extends StringColumn(this)

  object difficulty extends FloatColumn(this)

  object chainwork extends StringColumn(this)

  object previousblockhash extends StringColumn(this)

  object nextblockhash extends StringColumn(this)


  /*object hash extend StringColumn

  hash: String,
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
  nextblockhash: String
*/

  // Now the mapping function, transforming a row into a custom type.
  // This is a bit of boilerplate, but it's one time only and very short.
  def fromRow(row: Row): Block = {
    Block(
      id(row),
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

}
