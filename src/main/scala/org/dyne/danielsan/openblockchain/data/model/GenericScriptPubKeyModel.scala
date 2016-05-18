package org.dyne.danielsan.openblockchain.data.model

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.dyne.danielsan.openblockchain.data.entity.ScriptPubKey
import org.json4s._

import scala.concurrent.Future


/**
  * Created by dan_mi_sun on 30/03/2016.
  */

sealed class ScriptPubKeysModel extends CassandraTable[ScriptPubKeysModel, ScriptPubKey] {

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

  object hex extends StringColumn(this)

  object asm extends StringColumn(this)

  object `type` extends StringColumn(this)

  object reqSigs extends IntColumn(this)

  object addresses extends ListColumn[ScriptPubKeysModel, ScriptPubKey, String](this) with Index[List[String]]

}

abstract class ConcreteScriptPubKeysModel extends ScriptPubKeysModel with RootConnector {

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