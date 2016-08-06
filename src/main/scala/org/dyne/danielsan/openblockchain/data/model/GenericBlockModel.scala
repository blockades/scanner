package org.dyne.danielsan.openblockchain.data.model

import com.datastax.driver.core.{ResultSet, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.iteratee.Iteratee
import org.dyne.danielsan.openblockchain.data.entity.Block

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 27/02/2016.
  */

sealed class BlocksModel extends CassandraTable[BlocksModel, Block] {

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

  object height extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object confirmations extends IntColumn(this)

  object size extends IntColumn(this)

  object version extends IntColumn(this)

  object merkleroot extends StringColumn(this)

  object tx extends ListColumn[BlocksModel, Block, String](this)

  object time extends LongColumn(this)

  object nonce extends LongColumn(this)

  object bits extends StringColumn(this)

  object difficulty extends FloatColumn(this)

  object chainwork extends StringColumn(this)

  object previousblockhash extends StringColumn(this)

  object nextblockhash extends StringColumn(this)

}

abstract class ConcreteBlocksModel extends BlocksModel with RootConnector {

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

case class BlockTransactionCounts(hash: String, height: Int, num_transactions: Long)

sealed class BlockTransactionCountsModel extends CassandraTable[BlockTransactionCountsModel, BlockTransactionCounts] {

  override def tableName: String = "block_transaction_counts"

  override def fromRow(r: Row): BlockTransactionCounts = {
    BlockTransactionCounts(
      hash(r),
      height(r),
      num_transactions(r)
    )
  }

  object hash extends StringColumn(this) with PartitionKey[String]

  object height extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object num_transactions extends CounterColumn(this)

}

abstract class ConcreteBlockTransactionCountsModel extends BlockTransactionCountsModel with RootConnector {

  def increment(count: BlockTransactionCounts): Future[ResultSet] = {
    update
      .where(_.hash eqs count.hash)
      .and(_.height eqs count.height)
      .modify(_.num_transactions += count.num_transactions)
      .future()
  }

  def getCount(hash: String): Future[Option[Long]] = {
    select(_.num_transactions).where(_.hash eqs hash).one()
  }
}

case class BlockOpReturnTransactionCounts(hash: String, txid: String, num_op_return_transactions: Long)

sealed class BlockOpReturnTransactionCountsModel extends CassandraTable[BlockOpReturnTransactionCountsModel, BlockOpReturnTransactionCounts] {

  override def tableName: String = "block_op_return_transaction_counts"

  override def fromRow(r: Row): BlockOpReturnTransactionCounts = {
    BlockOpReturnTransactionCounts(
      hash(r),
      txid(r),
      num_op_return_transactions(r)
    )
  }

  object hash extends StringColumn(this) with PartitionKey[String]

  object txid extends StringColumn(this)  with ClusteringOrder[String] with Descending

  object num_op_return_transactions extends CounterColumn(this)

}

abstract class ConcreteBlockOpReturnTransactionCountsModel extends BlockOpReturnTransactionCountsModel with RootConnector {

  def increment(count: BlockOpReturnTransactionCounts): Future[ResultSet] = {
    update
      .where(_.hash eqs count.hash)
      .and(_.txid eqs count.txid)
      .modify(_.num_op_return_transactions += count.num_op_return_transactions)
      .future()
  }

  def getCount(hash: String): Future[Option[Long]] = {
    select(_.num_op_return_transactions).where(_.hash eqs hash).one()
  }
}