package org.dyne.danielsan.superchain.data.model

import com.datastax.driver.core.{ResultSet, Row}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.iteratee.Iteratee
import org.dyne.danielsan.superchain.data.entity.{Vin, Vout, Transaction}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.concurrent.Future


/**
  * Created by dan_mi_sun on 27/02/2016.
  */

sealed class TransactionsModel extends CassandraTable[TransactionsModel, Transaction] {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def fromRow(row: Row): Transaction = {
    Transaction(
      txid(row),
      version(row),
      locktime(row),
      vin(row),
      vout(row)
    )
  }

  object txid extends StringColumn(this) with PartitionKey[String]

  object version extends IntColumn(this)

  object locktime extends IntColumn(this)

  object vout extends JsonListColumn[TransactionsModel, Transaction, Vout](this) {
    override def fromJson(obj: String): Vout = {
      parse(obj).extract[Vout]
    }

    override def toJson(obj: Vout): String = {
      write(obj)
    }
  }

  object vin extends JsonListColumn[TransactionsModel, Transaction, Vin](this) {
    override def fromJson(obj: String): Vin = {
      parse(obj).extract[Vin]
    }

    override def toJson(obj: Vin): String = {
      write(obj)
    }
  }

}

abstract class ConcreteTransactionsModel extends TransactionsModel with RootConnector {

  override val tableName = "transactions"

  def insertNew(tx: Transaction): Future[ResultSet] = insertNewTransaction(tx).future()

  def insertNewTransaction(tx: Transaction) = {
    insert
      .value(_.txid, tx.txid)
      .value(_.version, tx.version)
      .value(_.locktime, tx.locktime)
      .value(_.vout, tx.vout)
      .value(_.vin, tx.vin)
  }

  def listAll = {
    select.fetchEnumerator() run Iteratee.collect()
  }

  def getByTxid(txid: String): Future[Option[Transaction]] = {
    select.where(_.txid eqs txid).one()
  }

}

