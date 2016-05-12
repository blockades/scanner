package org.dyne.danielsan.superchain.data.models

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.dyne.danielsan.superchain.data.entity.Vin
import org.json4s._

import scala.concurrent.Future



/**
  * Created by dan_mi_sun on 30/03/2016.
  */

sealed class VinColumnFamily extends CassandraTable[VinColumnFamily, Vin] {

  implicit val formats = DefaultFormats

  override def tableName: String = "vins"

  override def fromRow(row: Row): Vin = {
    Vin(
      coinbase(row),
      sequence(row)
    )
  }

  object coinbase extends StringColumn(this)

  object sequence extends IntColumn(this)
}

abstract class VinTable extends VinColumnFamily with RootConnector {

  override val tableName = "vins"

  def insertNew(v: Vin): Future[ResultSet] = insertNewVin(v).future()

  def insertNewVin(v: Vin) = {
    insert
      .value(_.coinbase, v.coinbase)
      .value(_.sequence, v.sequence)
  }
}