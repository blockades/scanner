package org.dyne.danielsan.superchain.data.models

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.concurrent.Future


/**
  * Created by dan_mi_sun on 27/02/2016.
  */

case class Transaction(txid: String,
                       version: Int,
                       locktime: Int,
                       vin: List[Vin],
                       vout: List[Vout]
                      )

sealed class TransactionColumnFamily extends CassandraTable[TransactionColumnFamily, Transaction] {

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

  object version extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object locktime extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object vout extends JsonListColumn[TransactionColumnFamily, Transaction, Vout](this) {
    override def fromJson(obj: String): Vout = {
      parse(obj).extract[Vout]
    }

    override def toJson(obj: Vout): String = {
      write(obj)
    }
  }

  object vin extends JsonListColumn[TransactionColumnFamily, Transaction, Vin](this) {
    override def fromJson(obj: String): Vin = {
      parse(obj).extract[Vin]
    }

    override def toJson(obj: Vin): String = {
      write(obj)
    }
  }

}

abstract class TransactionTable extends TransactionColumnFamily with RootConnector {

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

}

