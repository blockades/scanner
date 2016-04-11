package org.dyne.danielsan.superchain.data.models

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.json4s._

import scala.concurrent.Future


/**
  * Created by dan_mi_sun on 30/03/2016.
  */

case class ScriptPubKey(hex: String,
                        asm: String,
                        `type`: String,
                        reqSigs: Int,
                        addresses: List[String])

sealed class ScriptPubKeyColumnFamily extends CassandraTable[ScriptPubKeyColumnFamily, ScriptPubKey] {

  implicit val formats = DefaultFormats

  override def fromRow(row: Row): ScriptPubKey = {
    ScriptPubKey(
      hex(row),
      asm(row),
      `type`(row),
      reqSigs(row),
      addresses(row)
    )
  }

  object hex extends StringColumn(this) with PartitionKey[String]

  object asm extends StringColumn(this) with ClusteringOrder[String] with Descending

  object `type` extends StringColumn(this) with ClusteringOrder[String] with Descending

  object reqSigs extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object addresses extends ListColumn[ScriptPubKeyColumnFamily, ScriptPubKey, String](this) with Index[List[String]]

}

abstract class ScriptPubKeyTable extends ScriptPubKeyColumnFamily with RootConnector {

  override val tableName = "scriptpubkeys"

  def insertNew(spk: ScriptPubKey): Future[ResultSet] = insertNewScriptPubKey(spk).future()

  def insertNewScriptPubKey(spk: ScriptPubKey) = {
    insert
      .value(_.hex, spk.hex)
      .value(_.asm, spk.asm)
      .value(_.`type`, spk.`type`)
      .value(_.reqSigs, spk.reqSigs)
      .value(_.addresses, spk.addresses)
  }

}