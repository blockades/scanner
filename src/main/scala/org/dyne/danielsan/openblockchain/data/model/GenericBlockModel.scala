package org.dyne.danielsan.openblockchain.data.model

import com.datastax.driver.core.{ResultSet, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.dyne.danielsan.openblockchain.data.entity.{Block, BlockByHash}

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 27/02/2016.
  */
sealed class BlockModel extends CassandraTable[ConcreteBlockModel, Block] {

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
      Some(previousblockhash(row)),
      Some(nextblockhash(row)),
      Some(is_op_return(row))
    )
  }

  object hash extends StringColumn(this)

  object height extends IntColumn(this) with PartitionKey[Int]

  object confirmations extends IntColumn(this)

  object size extends IntColumn(this)

  object version extends IntColumn(this)

  object merkleroot extends StringColumn(this)

  object tx extends ListColumn[ConcreteBlockModel, Block, String](this)

  object time extends LongColumn(this)

  object nonce extends LongColumn(this)

  object bits extends StringColumn(this)

  object difficulty extends FloatColumn(this)

  object chainwork extends StringColumn(this)

  object previousblockhash extends StringColumn(this)

  object nextblockhash extends StringColumn(this)

  object is_op_return extends BooleanColumn(this)

}

abstract class ConcreteBlockModel extends BlockModel with RootConnector {

  override val tableName = "blocks"

  def insertNew(block: Block): Future[ResultSet] = insertNewRecord(block).future()

  def insertMany(list: List[Block]): Future[List[ResultSet]] = {
    Future.sequence(list.map(insertNewRecord(_).future()))
  }

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
      .value(_.previousblockhash, block.previousblockhash.getOrElse(""))
      .value(_.nextblockhash, block.nextblockhash.getOrElse(""))
      .value(_.is_op_return, block.is_op_return.get)
  }

  def getByHeight(height: Int): Future[Option[Block]] = {
    select.where(_.height eqs height).one()
  }

}

sealed class BlockByHashModel extends CassandraTable[ConcreteBlockByHashModel, BlockByHash] {

  override def fromRow(row: Row): BlockByHash = {
    BlockByHash(
      hash(row),
      height(row)
    )
  }

  object hash extends StringColumn(this) with PartitionKey[String]

  object height extends IntColumn(this)

}

abstract class ConcreteBlockByHashModel extends BlockByHashModel with RootConnector {

  override val tableName = "blocks_by_hash"

  def insertNew(block: BlockByHash): Future[ResultSet] = insertNewRecord(block).future()

  def insertMany(list: List[BlockByHash]): Future[List[ResultSet]] = {
    Future.sequence(list.map(insertNewRecord(_).future()))
  }

  def insertNewRecord(block: BlockByHash) = {
    insert
      .value(_.hash, block.hash)
      .value(_.height, block.height)
  }

  def getByHash(hash: String): Future[Option[BlockByHash]] = {
    select.where(_.hash eqs hash).one()
  }

}

