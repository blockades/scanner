package org.dyne.danielsan.superchain.data.models

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.json4s._
import org.json4s.jackson.JsonMethods._

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

case class Vin(coinbase: String,
               sequence: Int)

case class Vout(value: Float,
                n: Int,
                scriptPubKey: ScriptPubKey)

case class ScriptPubKey(hex: String,
                        asm: String,
                        `type`: String,
                        reqSigs: Int,
                        addresses: List[String])


/*
One of the issues that I am running into below is that:
"Cassandra collections do not allow custom data types. Storing JSON as a string is possible, but it's still a text column as far as Cassandra is concerned. The type in the below example is always a default C* type."

https://github.com/outworkers/phantom/wiki/Collection-columns
*/

sealed class TransactionColumnFamily extends CassandraTable[TransactionColumnFamily, Transaction] {

  implicit val formats = DefaultFormats

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

    //this is where we have gotten to again
    // need to figure out the serializer
    override def toJson(obj: Vout): String = {
      compact(render(obj))
    }
  }

  object vin extends JsonListColumn[TransactionColumnFamily, Transaction, Vin](this) {
    override def fromJson(obj: String): Vin = {
      parse(obj).extract[Vin]
    }

    override def toJson(obj: Vin): String = {
      compact(render(obj))
    }
  }

}

abstract class TransactionTable extends TransactionColumnFamily with RootConnector {

  override val tableName = "transactions"

  def insertNew(tx: Transaction): Future[ResultSet] = insertNewTransaction(tx).future()

  def insertNewTransaction(tx: Transaction) = {
    insert
      .value(_.txid, tx.txid)
      .value(_.vout, tx.vout)
      .value(_.version, tx.version)
      .value(_.vin, tx.vin)
      .value(_.locktime, tx.locktime)
  }

}

object TransactionColumnFamily extends TransactionColumnFamily with RootConnector {
//  figure out how to list -> this can come from other example working from
//    figure out how to insert https :// github.com / outworkers / phantom / wiki / Collection - columns

}

