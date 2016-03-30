package org.dyne.danielsan.superchain.data.models

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization.write



/**
  * Created by dan_mi_sun on 30/03/2016.
  */

case class Vout(value: Float,
                n: Int,
                scriptPubKey: List[ScriptPubKey])

sealed class VoutColumnFamily extends CassandraTable[VoutColumnFamily, Vout] {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def fromRow(row: Row): Vout = {
    Vout(
      value(row),
      n(row),
      scriptPubKey(row)
    )
  }

  object value extends FloatColumn(this) with PartitionKey[Float]

  object n extends IntColumn(this) with ClusteringOrder[Int] with Descending

  object scriptPubKey extends JsonListColumn[VoutColumnFamily, Vout, ScriptPubKey](this){
    override def fromJson(obj: String): ScriptPubKey = {
      parse(obj).extract[ScriptPubKey]
    }

    override def toJson(obj: ScriptPubKey): String = {
      write(obj)
    }
  }
}

abstract class VoutTable extends VoutColumnFamily with RootConnector {

  override val tableName = "vouts"

  def insertNewVout(v: Vout) = {
    insert
      .value(_.value, v.value)
      .value(_.n, v.n)
      .value(_.scriptPubKey, v.scriptPubKey)
  }
}
