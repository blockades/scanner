package org.dyne.danielsan.openblockchain.data.model

import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import org.dyne.danielsan.openblockchain.data.entity.Vin
import org.json4s._

import scala.concurrent.Future



/**
  * Created by dan_mi_sun on 30/03/2016.
  */

sealed class VinsModel extends CassandraTable[ConcreteVinsModel, Vin] {

  implicit val formats = DefaultFormats

  override def fromRow(row: Row): Vin = {
    Vin(
      coinbase(row),
      sequence(row)
    )
  }

  object coinbase extends OptionalStringColumn(this)

  object sequence extends OptionalIntColumn(this)
}

abstract class ConcreteVinsModel  extends VinsModel  with RootConnector {

  override val tableName = "vins"

  def insertNew(v: Vin): Future[ResultSet] = insertNewVin(v).future()

  def insertNewVin(v: Vin) = {
    insert
      .value(_.coinbase, v.coinbase)
      .value(_.sequence, v.sequence)
  }
}