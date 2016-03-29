package org.dyne.danielsan.superchain.data.models

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.concurrent.Future


/**
  * Created by dan_mi_sun on 27/02/2016.
  */

case class Transaction(blockhash: String,
                       blocktime: Long,
                       hex: String,
                       confirmations: Int,
                       txid: String,
                       vout: List[Vout],
                       version: Int,
                       vin: List[Vin],
                       time: Int,
                       locktime: Int)

case class Vout(value: Float,
                n: Int,
                scriptPubKey: ScriptPubKey)

case class ScriptPubKey(hex: String,
                        asm: String,
                        `type`: String,
                        reqSigs: Int,
                        addresses: List[String])

case class Vin(coinbase: String,
               sequence: Int)

/*
One of the issues that I am running into below is that:
"Cassandra collections do not allow custom data types. Storing JSON as a string is possible, but it's still a text column as far as Cassandra is concerned. The type in the below example is always a default C* type."

https://github.com/outworkers/phantom/wiki/Collection-columns

 */

sealed class TransactionColumnFamily extends CassandraTable[TransactionColumnFamily, Transaction] {

  implicit val formats = DefaultFormats

  override def fromRow(row: Row): Transaction = {
    Transaction(
      blockhash(row),
      blocktime(row),
      hex(row),
      confirmations(row),
      txid(row),
      vout(row),
      version(row),
      vin(row),
      time(row),
      locktime(row)
    )
  }

  object blockhash extends StringColumn(this) with PartitionKey[String]

  object blocktime extends LongColumn(this) with ClusteringOrder[Long] with Descending

  object hex extends StringColumn(this) with ClusteringOrder[String] with Descending

  object confirmations extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object txid extends StringColumn(this) with ClusteringOrder[String] with Descending

  object vout extends JsonListColumn[TransactionColumnFamily, Transaction, JValue](this) {
    override def fromJson(obj: String): JValue = {
      parse(obj).extract[JValue]
    }

    override def toJson(obj: JValue): String = {
      compact(render(obj))
    }
  }

  object version extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object vin extends JsonListColumn[TransactionColumnFamily, Transaction, JValue](this) {
    override def fromJson(obj: String): JValue = {
      parse(obj).extract[JValue]
    }

    override def toJson(obj: JValue): String = {
      compact(render(obj))
    }
  }

  object time extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object locktime extends IntColumn(this) with ClusteringOrder[Int] with Descending

}

abstract class TransactionTable extends TransactionColumnFamily with RootConnector {

  override val tableName = "transactions"

  def insertNew(tx: Transaction): Future[ResultSet] = insertNewTransaction(tx).future()

  def insertNewTransaction(tx: Transaction) = {
    insert
      .value(_.blockhash, tx.blockhash)
      .value(_.blocktime, tx.blocktime)
      .value(_.hex, tx.hex)
      .value(_.confirmations, tx.confirmations)
      .value(_.txid, tx.txid)
      .value(_.vout, tx.vout)
      .value(_.version, tx.version)
      .value(_.vin, tx.vin)
      .value(_.time, tx.time)
      .value(_.locktime, tx.locktime)
  }


}

//object TransactionColumnFamily extends TransactionColumnFamily with RootConnector {
////  figure out how to list -> this can come from other example working from
////    figure out how to insert https :// github.com / outworkers / phantom / wiki / Collection - columns
//
//}

