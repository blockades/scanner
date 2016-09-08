package org.dyne.danielsan.openblockchain.data.model

import com.datastax.driver.core.{ResultSet, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.iteratee.Iteratee
import org.dyne.danielsan.openblockchain.data.entity.{Transaction, Vin, Vout}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.concurrent.Future

/**
  * Created by dan_mi_sun on 27/02/2016.
  */

sealed class TransactionModel extends CassandraTable[ConcreteTransactionModel, Transaction] {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def fromRow(row: Row): Transaction = {
    Transaction(
      txid(row),
      hex(row),
      Some(size(row)),
      version(row),
      locktime(row),
      vin(row),
      vout(row),
      blockhash(row),
      confirmations(row),
      time(row),
      blocktime(row),
      Some(is_op_return(row))
    )
  }

  object txid extends StringColumn(this) with PartitionKey[String]

  object hex extends StringColumn(this)

  object size extends IntColumn(this)

  object version extends IntColumn(this)

  object locktime extends LongColumn(this)

  object vout extends JsonListColumn[ConcreteTransactionModel, Transaction, Vout](this) {
    override def fromJson(obj: String): Vout = {
      parse(obj).extract[Vout]
    }

    override def toJson(obj: Vout): String = {
      write(obj)
    }
  }

  object vin extends JsonListColumn[ConcreteTransactionModel, Transaction, Vin](this) {
    override def fromJson(obj: String): Vin = {
      parse(obj).extract[Vin]
    }

    override def toJson(obj: Vin): String = {
      write(obj)
    }
  }

  object blockhash extends StringColumn(this)

  object confirmations extends IntColumn(this)

  object time extends LongColumn(this)

  object blocktime extends LongColumn(this)

  object is_op_return extends BooleanColumn(this)

}

abstract class ConcreteTransactionModel extends TransactionModel with RootConnector {

  override val tableName = "transactions"

  def insertNew(tx: Transaction): Future[ResultSet] = insertNewTransaction(tx).future()

  def insertMany(list: List[Transaction]): Future[List[ResultSet]] = {
    Future.sequence(list.map(insertNewTransaction(_).future()))
  }

  def insertNewTransaction(tx: Transaction) = {
    insert
      .value(_.txid, tx.txid)
      .value(_.hex, tx.hex)
      .value(_.size, tx.size.getOrElse(0))
      .value(_.version, tx.version)
      .value(_.locktime, tx.locktime)
      .value(_.vout, tx.vout)
      .value(_.vin, tx.vin)
      .value(_.blockhash, tx.blockhash)
      .value(_.confirmations, tx.confirmations)
      .value(_.time, tx.time)
      .value(_.blocktime, tx.blocktime)
      .value(_.is_op_return, tx.is_op_return.get)
  }

  def listAll = {
    select.fetchEnumerator() run Iteratee.collect()
  }

  def getByTxid(txid: String): Future[Option[Transaction]] = {
    select.where(_.txid eqs txid).one()
  }

}
