package org.dyne.danielsan.openblockchain.data.model

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.dyne.danielsan.openblockchain.data.entity.{ScriptPubKey, Vout}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

/**
  * Created by dan_mi_sun on 30/03/2016.
  */
sealed class VoutModel extends CassandraTable[ConcreteVoutModel, Vout] {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def fromRow(row: Row): Vout = {
    Vout(
      value(row),
      n(row),
      scriptPubKey(row)
    )
  }

  object value extends FloatColumn(this)

  object n extends IntColumn(this)

  object scriptPubKey extends JsonColumn[ConcreteVoutModel, Vout, ScriptPubKey](this) {
    override def fromJson(obj: String): ScriptPubKey = {
      parse(obj).extract[ScriptPubKey]
    }

    override def toJson(obj: ScriptPubKey): String = {
      write(obj)
    }
  }

}

abstract class ConcreteVoutModel extends VoutModel with RootConnector {

  override val tableName = "vouts"

  def store(v: Vout) = {
    insert
      .value(_.value, v.value)
      .value(_.n, v.n)
      .value(_.scriptPubKey, v.scriptPubKey)
  }

}
